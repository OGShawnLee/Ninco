package ninco.common;

import ninco.business.AuthClient;
import ninco.business.dao.StoreDAO;
import ninco.business.dto.CartItemDTO;
import ninco.business.dto.EmployeeDTO;
import ninco.business.dto.StoreDTO;
import ninco.common.UserDisplayableException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReceiptGenerator {
    private static final Logger LOGGER = LogManager.getLogger(ReceiptGenerator.class);
    private static final String RECEIPT_DIR = "receipts";

    public static String generateReceipt(String clientName, List<CartItemDTO> items, double total) throws UserDisplayableException {
        EmployeeDTO employee = AuthClient.getInstance().getCurrentUser();
        StoreDTO store;
        try {
            store = StoreDAO.getInstance().getOne(employee.getIDStore());
        } catch (UserDisplayableException e) {
            try {
                store = new StoreDTO("Ninco Store", "Unknown Address", "000-0000");
            } catch (Exception ex) { throw new RuntimeException(ex); }
        }

        StringBuilder sb = new StringBuilder();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String timestamp = LocalDateTime.now().format(dtf);

        sb.append("************************************************\n");
        sb.append("                  NINCO STORE                   \n");
        sb.append("************************************************\n");
        sb.append(String.format("Store:   %s\n", store.getName()));
        sb.append(String.format("Address: %s\n", store.getAddress()));
        sb.append(String.format("Phone:   %s\n", store.getPhoneNumber()));
        sb.append(String.format("Date:    %s\n", timestamp));
        sb.append("************************************************\n");
        sb.append(String.format("CLIENT:  %s\n", clientName));
        sb.append("------------------------------------------------\n");
        sb.append(String.format("%-20s %5s %10s %10s\n", "PRODUCT", "QTY", "PRICE", "SUBTOTAL"));
        sb.append("------------------------------------------------\n");

        for (CartItemDTO item : items) {
            String name = item.getName();
            if (name.length() > 20) name = name.substring(0, 17) + "...";

            sb.append(String.format("%-20s %5d %10.2f %10.2f\n",
                    name,
                    item.getQuantity(),
                    item.getPrice(),
                    item.getSubtotal()));
        }

        sb.append("------------------------------------------------\n");
        sb.append(String.format("TOTAL:   %38.2f\n", total));
        sb.append("------------------------------------------------\n");
        sb.append(String.format("Cashier: %s %s\n", employee.getName(), employee.getLastName()));
        sb.append("************************************************\n");
        sb.append("        Thank you for your purchase!            \n");
        sb.append("************************************************\n");

        return saveToFile(sb.toString(), clientName);
    }

    private static String saveToFile(String content, String clientName) throws UserDisplayableException {
        File dir = new File(RECEIPT_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String filename = String.format("Receipt_%s_%d.txt",
                clientName.replaceAll("\\s+", "_"),
                System.currentTimeMillis());

        File file = new File(dir, filename);

        try (FileWriter fileWriter = new FileWriter(file);
             PrintWriter printWriter = new PrintWriter(fileWriter)) {

            printWriter.print(content);
            return file.getAbsolutePath();

        } catch (IOException e) {
            LOGGER.error("Error saving receipt file", e);
            throw new UserDisplayableException("Sale was successful, but receipt file could not be generated.");
        }
    }
}