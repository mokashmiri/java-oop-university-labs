package university;
import java.util.logging.Logger;
import java.util.*;

/**
 * This class represents a university education system.
 * 
 * It manages students and courses.
 *
 */
public class University {

	// Fields for university management
	private final String name;
	private String rectorFirst;
	private String rectorLast;
	
	// Student management
	private int nextStudentId = 10000;
	private final Map<Integer, Student> students = new HashMap<>();
	
	// Course management
	private int nextCourseId = 10;
	private final Map<Integer, Course> courses = new HashMap<>();
	
	// Registration management
	private final Map<Integer, Set<Integer>> studentCourses = new HashMap<>(); // studentId -> set of courseIds
	private final Map<Integer, Set<Integer>> courseStudents = new HashMap<>(); // courseId -> set of studentIds
	
	// Exam management
	private final Map<String, Integer> examGrades = new HashMap<>(); // "studentId-courseId" -> grade

// R1
	/**
	 * Constructor
	 * @param name name of the university
	 */
	public University(String name){
		this.name = name;
	}
	
	/**
	 * Getter for the name of the university
	 * 
	 * @return name of university
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Defines the rector for the university
	 * 
	 * @param first first name of the rector
	 * @param last	last name of the rector
	 */
	public void setRector(String first, String last){
		this.rectorFirst = first;
		this.rectorLast = last;
	}
	
	/**
	 * Retrieves the rector of the university with the format "First Last"
	 * 
	 * @return name of the rector
	 */
	public String getRector(){
		if (rectorFirst == null || rectorLast == null) {
			return null;
		}
		return rectorFirst + " " + rectorLast;
	}
	
// R2
	/**
	 * Enrol a student in the university
	 * The university assigns ID numbers 
	 * progressively from number 10000.
	 * 
	 * @param first first name of the student
	 * @param last last name of the student
	 * 
	 * @return unique ID of the newly enrolled student
	 */
	public int enroll(String first, String last){
		int studentId = nextStudentId++;
		Student student = new Student(studentId, first, last);
		students.put(studentId, student);
		studentCourses.put(studentId, new HashSet<>());
		
		logger.info(String.format("New student enrolled: %d, %s %s", studentId, first, last));
		return studentId;
	}
	
	/**
	 * Retrieves the information for a given student.
	 * The university assigns IDs progressively starting from 10000
	 * 
	 * @param id the ID of the student
	 * 
	 * @return information about the student
	 */
	public String student(int id){
		Student student = students.get(id);
		if (student == null) {
			return null;
		}
		return student.getId() + " " + student.getFirst() + " " + student.getLast();
	}
	
// R3
	/**
	 * Activates a new course with the given teacher
	 * Course codes are assigned progressively starting from 10.
	 * 
	 * @param title title of the course
	 * @param teacher name of the teacher
	 * 
	 * @return the unique code assigned to the course
	 */
	public int activate(String title, String teacher){
		int courseId = nextCourseId++;
		Course course = new Course(courseId, title, teacher);
		courses.put(courseId, course);
		courseStudents.put(courseId, new HashSet<>());
		
		logger.info(String.format("New course activated: %d, %s %s", courseId, title, teacher));
		return courseId;
	}
	
	/**
	 * Retrieve the information for a given course.
	 * 
	 * The course information is formatted as a string containing 
	 * code, title, and teacher separated by commas, 
	 * e.g., {@code "10,Object Oriented Programming,James Gosling"}.
	 * 
	 * @param code unique code of the course
	 * 
	 * @return information about the course
	 */
	public String course(int code){
		Course course = courses.get(code);
		if (course == null) {
			return null;
		}
		return course.getId() + "," + course.getTitle() + "," + course.getTeacher();
	}
	
// R4
	/**
	 * Register a student to attend a course
	 * @param studentID id of the student
	 * @param courseCode id of the course
	 */
	public void register(int studentID, int courseCode){
		if (students.containsKey(studentID) && courses.containsKey(courseCode)) {
			studentCourses.get(studentID).add(courseCode);
			courseStudents.get(courseCode).add(studentID);
			
			logger.info(String.format("Student %d signed up for course %d", studentID, courseCode));
		}
	}
	
	/**
	 * Retrieve a list of attendees.
	 * 
	 * The students appear one per row (rows end with `'\n'`) 
	 * and each row is formatted as describe in in method {@link #student}
	 * 
	 * @param courseCode unique id of the course
	 * @return list of attendees separated by "\n"
	 */
	public String listAttendees(int courseCode){
		Set<Integer> attendees = courseStudents.get(courseCode);
		if (attendees == null || attendees.isEmpty()) {
			return "";
		}
		
		StringBuilder result = new StringBuilder();
		for (int studentId : attendees) {
			if (result.length() > 0) {
				result.append("\n");
			}
			result.append(student(studentId));
		}
		return result.toString();
	}

	/**
	 * Retrieves the study plan for a student.
	 * 
	 * The study plan is reported as a string having
	 * one course per line (i.e. separated by '\n').
	 * The courses are formatted as describe in method {@link #course}
	 * 
	 * @param studentID id of the student
	 * 
	 * @return the list of courses the student is registered for
	 */
	public String studyPlan(int studentID){
		Set<Integer> studentCourseIds = studentCourses.get(studentID);
		if (studentCourseIds == null || studentCourseIds.isEmpty()) {
			return "";
		}
		
		StringBuilder result = new StringBuilder();
		for (int courseId : studentCourseIds) {
			if (result.length() > 0) {
				result.append("\n");
			}
			result.append(course(courseId));
		}
		return result.toString();
	}

// R5
	/**
	 * records the grade (integer 0-30) for an exam can 
	 * 
	 * @param studentId the ID of the student
	 * @param courseID	course code 
	 * @param grade		grade ( 0-30)
	 */
	public void exam(int studentId, int courseID, int grade) {
		if (students.containsKey(studentId) && courses.containsKey(courseID)) {
			String examKey = studentId + "-" + courseID;
			examGrades.put(examKey, grade);
			
			logger.info(String.format("Student %d took an exam in course %d with grade %d", studentId, courseID, grade));
		}
	}

	/**
	 * Computes the average grade for a student and formats it as a string
	 * using the following format 
	 * 
	 * {@code "Student STUDENT_ID : AVG_GRADE"}. 
	 * 
	 * If the student has no exam recorded the method
	 * returns {@code "Student STUDENT_ID hasn't taken any exams"}.
	 * 
	 * @param studentId the ID of the student
	 * @return the average grade formatted as a string.
	 */
	public String studentAvg(int studentId) {
		List<Integer> grades = new ArrayList<>();
		
		// Find all grades for this student
		for (Map.Entry<String, Integer> entry : examGrades.entrySet()) {
			if (entry.getKey().startsWith(studentId + "-")) {
				grades.add(entry.getValue());
			}
		}
		
		if (grades.isEmpty()) {
			return "Student " + studentId + " hasn't taken any exams";
		}
		
		double sum = 0;
		for (int grade : grades) {
			sum += grade;
		}
		double average = sum / grades.size();
		
		return "Student " + studentId + " : " + String.format("%.1f", average);
	}
	
	/**
	 * Computes the average grades of all students that took the exam for a given course.
	 * 
	 * The format is the following: 
	 * {@code "The average for the course COURSE_TITLE is: COURSE_AVG"}.
	 * 
	 * If no student took the exam for that course it returns {@code "No student has taken the exam in COURSE_TITLE"}.
	 * 
	 * @param courseId	course code 
	 * @return the course average formatted as a string
	 */
	public String courseAvg(int courseId) {
		Course course = courses.get(courseId);
		if (course == null) {
			return "No student has taken the exam in Unknown Course";
		}
		
		List<Integer> grades = new ArrayList<>();
		
		// Find all grades for this course
		for (Map.Entry<String, Integer> entry : examGrades.entrySet()) {
			if (entry.getKey().endsWith("-" + courseId)) {
				grades.add(entry.getValue());
			}
		}
		
		if (grades.isEmpty()) {
			return "No student has taken the exam in " + course.getTitle();
		}
		
		double sum = 0;
		for (int grade : grades) {
			sum += grade;
		}
		double average = sum / grades.size();
		
		return "The average for the course " + course.getTitle() + " is: " + String.format("%.1f", average);
	}
	

// R6
	/**
	 * Retrieve information for the best students to award a price.
	 * 
	 * The students' score is evaluated as the average grade of the exams they've taken. 
	 * To take into account the number of exams taken and not only the grades, 
	 * a special bonus is assigned on top of the average grade: 
	 * the number of taken exams divided by the number of courses the student is enrolled to, multiplied by 10.
	 * The bonus is added to the exam average to compute the student score.
	 * 
	 * The method returns a string with the information about the three students with the highest score. 
	 * The students appear one per row (rows are terminated by a new-line character {@code '\n'}) 
	 * and each one of them is formatted as: {@code "STUDENT_FIRSTNAME STUDENT_LASTNAME : SCORE"}.
	 * 
	 * @return info on the best three students.
	 */
	public String topThreeStudents() {
		List<StudentScore> studentScores = new ArrayList<>();
		
		for (Student student : students.values()) {
			int studentId = student.getId();
			
			// Calculate exam average
			List<Integer> grades = new ArrayList<>();
			for (Map.Entry<String, Integer> entry : examGrades.entrySet()) {
				if (entry.getKey().startsWith(studentId + "-")) {
					grades.add(entry.getValue());
				}
			}
			
			if (grades.isEmpty()) {
				continue; // Skip students with no exams
			}
			
			double sum = 0;
			for (int grade : grades) {
				sum += grade;
			}
			double examAverage = sum / grades.size();
			
			// Calculate bonus
			int enrolledCourses = studentCourses.get(studentId).size();
			double bonus = (double) grades.size() / enrolledCourses * 10;
			
			// Calculate final score
			double finalScore = examAverage + bonus;
			
			studentScores.add(new StudentScore(student, finalScore));
		}
		
		// Sort by score (descending)
		studentScores.sort((a, b) -> Double.compare(b.score, a.score));
		
		// Take top 3
		StringBuilder result = new StringBuilder();
		int count = Math.min(3, studentScores.size());
		for (int i = 0; i < count; i++) {
			if (result.length() > 0) {
				result.append("\n");
			}
			StudentScore ss = studentScores.get(i);
			result.append(ss.student.getFirst() + " " + ss.student.getLast() + " : " + String.format("%.1f", ss.score));
		}
		
		return result.toString();
	}

// R7
    /**
     * This field points to the logger for the class that can be used
     * throughout the methods to log the activities.
     */
    public static final Logger logger = Logger.getLogger("University");

	// Inner classes
	private static class Student {
		private int id;
		private String first;
		private String last;
		
		public Student(int id, String first, String last) {
			this.id = id;
			this.first = first;
			this.last = last;
		}
		
		public int getId() { return id; }
		public String getFirst() { return first; }
		public String getLast() { return last; }
	}
	
	private static class Course {
		private int id;
		private String title;
		private String teacher;
		
		public Course(int id, String title, String teacher) {
			this.id = id;
			this.title = title;
			this.teacher = teacher;
		}
		
		public int getId() { return id; }
		public String getTitle() { return title; }
		public String getTeacher() { return teacher; }
	}
	
	private static class StudentScore {
		private Student student;
		private double score;
		
		public StudentScore(Student student, double score) {
			this.student = student;
			this.score = score;
		}
	}

}
