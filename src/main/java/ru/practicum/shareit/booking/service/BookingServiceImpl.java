package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingSufficiencyDto;
import ru.practicum.shareit.booking.enums.BookingPeriod;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.interfaces.BookingService;
import ru.practicum.shareit.booking.interfaces.BookingStorage;
import ru.practicum.shareit.booking.mappers.BookingMapper;
import ru.practicum.shareit.booking.models.Booking;
import ru.practicum.shareit.exceptions.IncorrectDataException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.interfaces.ItemStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.interfaces.UserStorage;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingStorage bookingStorage;
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;


    @Override
    @Transactional
    public BookingSufficiencyDto approve(Long ownerId, Long bookingId, boolean approved) {
        Booking booking = bookingStorage.findById(bookingId).orElseThrow(() -> new NotFoundException(
                String.format("Booking with ID=%d cannot be found", bookingId)));

        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new NotFoundException("Only the item owner can approve or reject the rental request.");
        }

        if ((!booking.getStatus().equals(BookingStatus.WAITING))) {
            throw new IncorrectDataException("Only status [WAITING] can be updated");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        return BookingMapper.mapToBookingSufficiencyDto(bookingStorage.save(booking));
    }

    @Override
    @Transactional
    public BookingSufficiencyDto save(Long userId, BookingDto bookingDto) {


        Item item = itemStorage.findById(bookingDto.getItemId()).orElseThrow(
                () -> new NotFoundException("item with id" + bookingDto.getItemId() + "cannot found"));
        User booker = userStorage.findById(userId).orElseThrow(
                () -> new NotFoundException("item with id" + bookingDto.getItemId() + "cannot found"));

        if (Boolean.FALSE.equals(item.getAvailable())) {
            throw new IncorrectDataException("That item: " + item.getId() + " cannot be booked");
        }

        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("That item: " + item.getId() + " cannot be booked because it's been take by owner");
        }

        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new IncorrectDataException("Booking date end cannot be before start date");
        }

        if (bookingDto.getEnd().isEqual(bookingDto.getStart())) {
            throw new IncorrectDataException("Booking date end cannot be ident start date");
        }

        Booking booking = BookingMapper.mapToBooking(bookingDto);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        booking.setBooker(booker);

        return BookingMapper.mapToBookingSufficiencyDto(bookingStorage.save(booking));
    }

    @Override
    public BookingSufficiencyDto getById(Long userId, Long bookingId) {
        Booking booking = bookingStorage.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Booking with ID=%d cannot be found", bookingId)));

        if (!booking.getItem().getOwner().getId().equals(userId) && !booking.getBooker().getId().equals(userId)) {
            throw new NotFoundException("The item should belong to the item owner or the booking author.");
        }

        return BookingMapper.mapToBookingSufficiencyDto(booking);
    }


    @Override
    public List<BookingSufficiencyDto> getByOwner(Long ownerId, String state) {

        userStorage.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User with id" + ownerId + "cannot found"));

        if (state == null || BookingPeriod.ALL.name().equals(state)) {
            List<Booking> booking = bookingStorage.findAllByItemOwnerIdOrderByStartDesc(ownerId);
            return BookingMapper.mapToBookingSufficiencyDto(booking);
        }

        if (BookingPeriod.FUTURE.name().equals(state)) {
            return BookingMapper.mapToBookingSufficiencyDto(
                    bookingStorage.findByItemOwnerIdAndStartIsAfterAndEndIsAfterOrderByStartDesc(
                            ownerId, LocalDateTime.now(), LocalDateTime.now()));
        }

        if (BookingPeriod.PAST.name().equals(state)) {
            return BookingMapper.mapToBookingSufficiencyDto(
                    bookingStorage.findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(
                            ownerId, LocalDateTime.now()));
        }

        if (BookingPeriod.CURRENT.name().equals(state)) {
            return BookingMapper.mapToBookingSufficiencyDto(
                    bookingStorage.findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                            ownerId, LocalDateTime.now(), LocalDateTime.now()));
        }


        /* Мне этот вариант реализации показался наиболее рациональным т.к. всё,
         что мне удалось найти, предполагало наличие цикла или вспомогательных методов
         подобных EnumUtils.isValidEnum(MyEnum.class, myValue)*/

        boolean checkEnum = Arrays.stream(BookingStatus.values())
                .anyMatch(status -> status.name()
                        .equals(state));

        if (checkEnum) {
            return BookingMapper.mapToBookingSufficiencyDto(bookingStorage
                    .findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.valueOf(state)));
        } else {
            throw new IncorrectDataException("Unknown state: " + state);
        }
    }

    @Override
    public List<BookingSufficiencyDto> getByBookerId(Long bookerId, String state) {

        itemStorage.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("item with id" + bookerId + "cannot found"));

        if (state == null || BookingPeriod.ALL.name().equals(state)) {
            return BookingMapper.mapToBookingSufficiencyDto(bookingStorage.findByBookerIdOrderByStartDesc(bookerId));
        }

        if (BookingPeriod.FUTURE.name().equals(state)) {
            return BookingMapper.mapToBookingSufficiencyDto(
                    bookingStorage.findAllByBookerIdAndStartIsAfterAndEndIsAfterOrderByStartDesc(
                            bookerId, LocalDateTime.now(), LocalDateTime.now()));
        }

        if (BookingPeriod.PAST.name().equals(state)) {
            return BookingMapper.mapToBookingSufficiencyDto(
                    bookingStorage.findByBookerIdAndEndIsBeforeOrderByStartDesc(
                            bookerId, LocalDateTime.now()));
        }

        if (BookingPeriod.CURRENT.name().equals(state)) {
            return BookingMapper.mapToBookingSufficiencyDto(
                    bookingStorage.findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                            bookerId, LocalDateTime.now(), LocalDateTime.now()));
        }


        //из - за необходимости константных переменных у меня не получилось преобразовать if в switch

        /*switch (BookingPeriod.valueOf(state)) {
            case(FUTURE):
                return BookingMapper.mapToBookingSufficiencyDto(
                        bookingStorage.findAllByBookerIdAndStartIsAfterAndEndIsAfterOrderByStartDesc(
                                bookerId, LocalDateTime.now(), LocalDateTime.now()));
                break;
            case (PAST):
                return BookingMapper.mapToBookingSufficiencyDto(
                        bookingStorage.findByBookerIdAndEndIsBeforeOrderByStartDesc(
                                bookerId, LocalDateTime.now()));
                break;
            case(CURRENT):
                return BookingMapper.mapToBookingSufficiencyDto(
                        bookingStorage.findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                                bookerId, LocalDateTime.now(), LocalDateTime.now()));
                break;
        }
         */


        if (Arrays.stream(BookingStatus.values()).anyMatch(status -> status.name().equals(state))) {
            return BookingMapper.mapToBookingSufficiencyDto(bookingStorage
                    .findByBookerIdAndStatusOrderByStartDesc(bookerId, BookingStatus.valueOf(state)));
        } else {
            throw new IncorrectDataException("Unknown state: " + state);
        }
    }
}