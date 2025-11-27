class Grade {
    private int gradeId;
    private int enrollmentId;
    private String component; // Quiz, Midterm, EndSem, etc.
    private Double score;
    private String finalGrade; // A, B+, B, etc.
    
    public Grade() {}
    
    public Grade(int enrollmentId, String component, Double score) {
        this.enrollmentId = enrollmentId;
        this.component = component;
        this.score = score;
    }
    
    // Getters and Setters
    public int getGradeId() { return gradeId; }
    public void setGradeId(int gradeId) { this.gradeId = gradeId; }
    public int getEnrollmentId() { return enrollmentId; }
    public void setEnrollmentId(int enrollmentId) { this.enrollmentId = enrollmentId; }
    public String getComponent() { return component; }
    public void setComponent(String component) { this.component = component; }
    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }
    public String getFinalGrade() { return finalGrade; }
    public void setFinalGrade(String finalGrade) { this.finalGrade = finalGrade; }
}
