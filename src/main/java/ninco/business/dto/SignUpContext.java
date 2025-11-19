package ninco.business.dto;

public class SignUpContext {
    private final EmployeeDTO employeeDTO;
    private final String rawPassword; // Necesaria para crear la cuenta final

    public SignUpContext(EmployeeDTO employeeDTO, String rawPassword) {
        this.employeeDTO = employeeDTO;
        this.rawPassword = rawPassword;
    }

    public EmployeeDTO getEmployeeDTO() { return employeeDTO; }
    public String getRawPassword() { return rawPassword; }
}
