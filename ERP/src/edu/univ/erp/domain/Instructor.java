class Instructor {
    private int userId;
    private String employeeId;
    private String name;
    private String department;
    private String email;
    
    public Instructor() {}
    
    public Instructor(int userId, String employeeId, String name, String department, String email) {
        this.userId = userId;
        this.employeeId = employeeId;
        this.name = name;
        this.department = department;
        this.email = email;
    }
    
    // Getters and Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}