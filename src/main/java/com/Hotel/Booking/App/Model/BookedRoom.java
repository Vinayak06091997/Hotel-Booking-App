package com.Hotel.Booking.App.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookedRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;

    @Column(name="check_In")
    private LocalDate checkInDate;

    @Column(name="check_Out")
    private LocalDate checkOutDate;

    @Column(name="guest_Full_Name")
    private String guestFullName;

    @Column(name="guest_Email")
    private String guestEmail;

    @Column(name="adults")
    private int NumOfAdults;

    @Column(name="children")
    private int NumOfChildren;

    @Column(name="total_guest")
    private int totalNumOfGuest;

    @Column(name="confirmation_Code")
    private String bookingConfirmationCode;

    @ManyToOne(fetch =FetchType.LAZY)

    @JoinColumn(name="room_Id")
    private Room room;


    public void calculateTotalNumber()
    {
       this.totalNumOfGuest = this.NumOfAdults + this.NumOfChildren;
    }



    public void setNumOfAdults(int numOfAdults) {
        NumOfAdults = numOfAdults;
        calculateTotalNumber();
    }

    public void setNumOfChildren(int numOfChildren) {
        NumOfChildren = numOfChildren;
        calculateTotalNumber();
    }

    public void setBookingConfirmationCode(String bookingConfirmationCode) {
        this.bookingConfirmationCode = bookingConfirmationCode;
    }
}
