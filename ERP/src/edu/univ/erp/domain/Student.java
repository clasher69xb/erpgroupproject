    





class Student {
    private int userId;
    private String rollNo;
    private String name;
    private String program;
    private int year;
    private String email;
    
    public Student() {}
    
    public Student(int userId, String rollNo, String name, String program, int year, String email) {
        this.userId = userId;
        this.rollNo = rollNo;
        this.name = name;
        this.program = program;
        this.year = year;
        this.email = email;
    }
    
    // Getters and Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getRollNo() { return rollNo; }
    public void setRollNo(String rollNo) { this.rollNo = rollNo; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getProgram() { return program; }
    public void setProgram(String program) { this.program = program; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
