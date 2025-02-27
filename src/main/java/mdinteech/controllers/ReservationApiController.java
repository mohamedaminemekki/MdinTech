//package mdinteech.controllers;
//
//import mdinteech.entities.Reservation;
//import mdinteech.services.ReservationService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.sql.SQLException;
//
//@RestController
//@RequestMapping("/api/reservations")
//public class ReservationApiController { // Renommez le contrôleur
//
//    @Autowired
//    private ReservationService reservationService; // Utilisez votre service existant
//
//    @GetMapping("/{id}")
//    public ResponseEntity<Reservation> getReservationById(@PathVariable int id) {
//        Reservation reservation = null;
//        try {
//            reservation = reservationService.getById(id);
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//        if (reservation != null) {
//            return ResponseEntity.ok(reservation);
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }
//}
