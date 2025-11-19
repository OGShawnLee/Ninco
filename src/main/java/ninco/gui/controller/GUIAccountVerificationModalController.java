package ninco.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import ninco.business.dao.EmployeeDAO;
import ninco.business.dao.PendingRegistrationsDAO;
import ninco.business.dto.SignUpContext;
import ninco.common.UserDisplayableException;
import ninco.gui.AlertFacade;

public class GUIAccountVerificationModalController extends Controller implements ContextController<SignUpContext> {

    @FXML private TextField fieldPin;
    @FXML private Label labelError;

    private SignUpContext context;

    @Override
    public void setContext(SignUpContext data) {
        this.context = data;
    }

    public void onClickVerify() {
        try {
            labelError.setText("");
            String enteredPin = fieldPin.getText();

            // Validación compatible con Java 8 (sin isBlank)
            if (enteredPin == null || enteredPin.trim().isEmpty()) {
                labelError.setText("Por favor introduzca el código.");
                return;
            }

            enteredPin = enteredPin.trim();
            String email = context.getEmployeeDTO().getEmail();

            // Verificar contra la base de datos (auditoría)
            boolean isValid = PendingRegistrationsDAO.getInstance().verifyPin(email, enteredPin);

            if (!isValid) {
                labelError.setText("Código incorrecto o expirado.");
                return;
            }

            // Crear la cuenta real
            EmployeeDAO.getInstance().createOne(
                    context.getEmployeeDTO(),
                    context.getRawPassword()
            );

            AlertFacade.showSuccessAndWait("¡Cuenta verificada y creada exitosamente!");
            close();

        } catch (UserDisplayableException e) {
            AlertFacade.showErrorAndWait(e.getMessage());
        }
    }
}