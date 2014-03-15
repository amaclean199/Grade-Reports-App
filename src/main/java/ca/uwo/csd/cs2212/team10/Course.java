package ca.uwo.csd.cs2212.team10;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a course in the Gradebook
 *
 * @author Team 10
 */
public class Course implements Serializable {
    /* Constants */

    private static final long serialVersionUID = 1L; //for serializing

    /* Attributes */
    private String code;
    private String term;
    private String title;
    private List<Student> students;
    private List<Deliverable> deliverables;

    /* Constructor */
    public Course(String title, String code, String term) {
        this.title = title;
        this.code = code;
        this.term = term;

        students = new ArrayList<Student>();
        deliverables = new ArrayList<Deliverable>();
    }

    /* Public Methods */
    public void setTitle(String title) {
        this.title = title;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getTitle() {
        return title;
    }

    public String getCode() {
        return code;
    }

    public String getTerm() {
        return term;
    }

    public List<Student> getStudentList() {
        return students;
    }

    public List<Deliverable> getDeliverableList() {
        return deliverables;
    }

    public boolean isUnique(Student student){
      for (Student stdnt : students)
        if (student != stdnt)
          if (student.getEmail().equals(stdnt.getEmail()) || student.getNum().equals(stdnt.getNum()))
            return false;
      return true;
    }
    
    public void  addStudent(Student student) throws DuplicateStudentException{
        
        //Check that new student has unique student number and email
        if (!(isUnique(student)))
                throw new DuplicateStudentException("Student info not unique");
       
        students.add(student);

        //Add each deliverable to the grade list
        for (Deliverable deliverable : deliverables)
            student.setGrade(deliverable, 0.0);
    }

    public void removeStudent(Student student) {
        students.remove(student);
    }

    public void addDeliverable(Deliverable deliverable) {
        deliverables.add(deliverable);
        
        //Add the deliverable to the grade list of each student
        for (Student student : students)
            student.setGrade(deliverable, 0.0);
    }

    public void removeDeliverable(Deliverable deliverable) {
        deliverables.remove(deliverable);
        
        //Remove the deliverable from the grade list of each student
        for (Student student : students)
            student.removeGrade(deliverable);
    }

    @Override
    public String toString() {
        return code + " - " + term + " - " + title;
    }
}
