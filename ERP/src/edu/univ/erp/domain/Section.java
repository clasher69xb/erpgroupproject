class Section {
    private int sectionId;
    private int courseId;
    private Integer instructorId;
    private String courseName;
    private String instructorName;
    private String dayTime;
    private String room;
    private int capacity;
    private int enrolled;
    private String semester;
    private int year;
    
    public Section() {}
    
    // Getters and Setters
    public int getSectionId() { return sectionId; }
    public void setSectionId(int sectionId) { this.sectionId = sectionId; }
    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }
    public Integer getInstructorId() { return instructorId; }
    public void setInstructorId(Integer instructorId) { this.instructorId = instructorId; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public String getInstructorName() { return instructorName; }
    public void setInstructorName(String instructorName) { this.instructorName = instructorName; }
    public String getDayTime() { return dayTime; }
    public void setDayTime(String dayTime) { this.dayTime = dayTime; }
    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public int getEnrolled() { return enrolled; }
    public void setEnrolled(int enrolled) { this.enrolled = enrolled; }
    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    
    public boolean isFull() {
        return enrolled >= capacity;
    }
}
