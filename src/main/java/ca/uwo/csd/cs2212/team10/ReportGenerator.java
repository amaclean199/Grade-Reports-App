package ca.uwo.csd.cs2212.team10;

import java.io.*;
import java.util.*;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.*;
import net.sf.jasperreports.engine.design.*;
import net.sf.jasperreports.engine.xml.*;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

public class ReportGenerator {
    private final static String REPORT_FILENAME = "grade_report.jrxml";

    /* Attributes */
    JasperReport report;

    public ReportGenerator() {
        try { 
            InputStream reportStream = ReportGenerator.class.getClassLoader().getResourceAsStream(REPORT_FILENAME);
            JasperDesign jasperDesign = JRXmlLoader.load(reportStream);
            report = JasperCompileManager.compileReport(jasperDesign);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    
    private JasperPrint fillReport(Course course, Student student) throws JRException {
        Collection<JavaBean> beans = new ArrayList<JavaBean>();
        List<Deliverable> deliverables = course.getDeliverableList();
        
        for (Deliverable d : deliverables)
            beans.add(new JavaBean(d.getName(), student.getGrade(d), course.calcAverage(d)));
        
        JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(beans);
        
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("courseTitle", course.getTitle());
        parameters.put("courseCode", course.getCode());
        parameters.put("courseTerm", course.getTerm());
        parameters.put("firstName", student.getFirstName());
        parameters.put("lastName", student.getLastName());
        parameters.put("email", student.getEmail());
        parameters.put("number", student.getNum());
        parameters.put("average", student.calcAverage());
        parameters.put("asnAverage", student.calcAverage(Deliverable.ASSIGNMENT_TYPE));
        parameters.put("examAverage", student.calcAverage(Deliverable.EXAM_TYPE));

        return JasperFillManager.fillReport(report, parameters, beanColDataSource);
    }

    public void exportPDF(String directory, Course course, List<Student> students) throws JRException {
        for (Student s : students){ 
            JasperExportManager.exportReportToPdfFile(fillReport(course, s), directory + s.getLastName() + "-" + s.getFirstName() + "-" + s.getNum() + ".pdf");
        }
    }

    public void sendReportByEmail(String smtpServer, final String username, final String password, String fromAddress,
            Course course, List<Student> students) throws Exception {
        
        Properties props = new Properties();
 
        props.put("mail.smtp.host", smtpServer);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props,
            new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            }
        );
            
        for (Student s : students){
            
            Message msg = new MimeMessage(session);

            Address sender = new InternetAddress(fromAddress);
            msg.setFrom(sender);

            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(s.getEmail()));

            msg.setSubject("Grade Report");

            Multipart multiPart = new MimeMultipart();

            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(loadTemplate("email.text.vm", s.getFirstName()), "utf -8");

            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(loadTemplate("email.html.vm", s.getFirstName()), "text/html; charset =utf -8");

            MimeBodyPart fileAttachmentPart = new MimeBodyPart();

            ByteArrayOutputStream output = new ByteArrayOutputStream();
            JasperExportManager.exportReportToPdfStream(fillReport(course, s), output);
            DataSource source = new ByteArrayDataSource(output.toByteArray(), "application/pdf");
            
            fileAttachmentPart.setDataHandler(new DataHandler(source));
            fileAttachmentPart.setFileName("Grade_Report.pdf");

            multiPart.addBodyPart(textPart);
            multiPart.addBodyPart(htmlPart);
            multiPart.addBodyPart(fileAttachmentPart);

            msg.setContent(multiPart);

            Transport.send(msg);
        }
    } 
    
    private static String loadTemplate(String filename, String name) {
      
      VelocityEngine ve = new VelocityEngine();
      ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
      ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
              
      Template template = ve.getTemplate(filename);
      
      VelocityContext context = new VelocityContext();
      context.put("studentName", name);
      
      StringWriter out = new StringWriter();
      template.merge(context, out);
      
      return out.toString();
  }
}

