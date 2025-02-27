package mdinteech.controllers;

import com.google.zxing.WriterException;
import mdinteech.services.QRCodeService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class QRCodeController {

    /**
     * Endpoint pour générer un QR code contenant les détails de la réservation.
     *
     * @param reservationId L'ID de la réservation.
     * @param userId        L'ID de l'utilisateur.
     * @param tripId        L'ID du trajet.
     * @param seatNumber    Le numéro de siège.
     * @param width         La largeur du QR code (par défaut 250).
     * @param height        La hauteur du QR code (par défaut 250).
     * @return Une image PNG du QR code.
     */
    @GetMapping(value = "/api/qrcode", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> generateQRCode(
            @RequestParam String reservationId,
            @RequestParam String userId,
            @RequestParam String tripId,
            @RequestParam String seatNumber,
            @RequestParam(defaultValue = "250") int width,
            @RequestParam(defaultValue = "250") int height) throws IOException, WriterException, WriterException {

        // Créer une chaîne de données pour le QR code
        String qrCodeText = String.format(
                "Reservation ID: %s\nUser ID: %s\nTrip ID: %s\nSeat Number: %s",
                reservationId, userId, tripId, seatNumber
        );

        // Générer le QR code
        byte[] qrCodeImage = QRCodeService.generateQRCodeImage(qrCodeText, width, height);

        // Retourner l'image du QR code
        return ResponseEntity.ok().body(qrCodeImage);
    }
}