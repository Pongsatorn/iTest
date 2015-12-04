package app
import com.vaadin.grails.Grails
import com.vaadin.ui.*
import com.vaadin.server.*
import com.vaadin.data.Property;
import com.vaadin.ui.*
import javax.servlet.*
import com.vaadin.server.VaadinServlet
import com.vaadin.annotations.Theme
import com.vaadin.annotations.Title
import com.vaadin.shared.ui.label.ContentMode
import test.*
import com.vaadin.ui.Table
import com.vaadin.ui.Table.ColumnGenerator
import com.vaadin.ui.Table.ColumnResizeEvent
import com.vaadin.ui.Table.HeaderClickEvent
import com.vaadin.ui.TableFieldFactory
class Controller {
	VerticalLayout todoLayout
	Table table
	void addEnrollmentItem(String title,String studentId,String pass) {
		int count = 0;
		def list = Subject.executeQuery("from Subject")	
		//Subject subject
		for(s in list){
			if(title == s.subjectName)
				count = 1;
		}
		if(count == 0){
			Enrollment enrollment = new Enrollment()
			enrollment.setStudent(new test.Student(name: studentId,password: pass).save())
			enrollment.setSubject(new test.Subject(subjectName: title).save())
			enrollment.setType("เพิ่มรายวิชา")
			enrollment = enrollment.save()
		}
		else{
				new Notification("ขออภัยครับ",
        		"รายวิชานี้ลงทะเบียนไว้แล้ว กรุณาตรวจสอบที่รายการที่ลงทะเบียนไว้",
        		Notification.Type.WARNING_MESSAGE).show(Page.getCurrent())
			}
			table.removeAllItems()
			loadAllLabOrder()
	}
	void dropEnrollmentItem(String drop,String studentId,String pass){
		int checkId
		def list = Enrollment.executeQuery("from Enrollment")
		for (checkSubject in list){
			if(checkSubject.subject.subjectName == drop){
				 checkId = checkSubject.subject.id
			}
		}
			def delSubject = Subject.get(checkId)
			if(delSubject != null){
	        	delSubject.delete(flush: true)
	        	Enrollment enrollment = new Enrollment()
				enrollment.setStudent(new test.Student(name: studentId,password:pass).save())
				enrollment.setSubject(new test.Subject(subjectName: drop).save())
				enrollment.setType("ลดรายวิชา")
				enrollment = enrollment.save()
            }
            else{
					new Notification("ขออภัยครับ",
            		"รายวิชานี้ยังไม่ได้ลงทะเบียนไว้ กรุณาตรวจสอบที่รายการที่ลงทะเบียนไว้",
            		Notification.Type.WARNING_MESSAGE).show(Page.getCurrent())
			}
            table.removeAllItems()
			loadAllLabOrder()
	}
	void loadAllEnrollments() {
		def list = Enrollment.executeQuery("from Enrollment")
		for(enrollment in list) {
			todoLayout.addComponent(new Label( enrollment.subject.subjectName 
				+" (${enrollment.student.name})"))
		}
	}
	void searchEnrollments(String query) {
		String[] miss = ""
		def list = Enrollment.executeQuery(
			"from Enrollment where subject.subjectName like :q", [q: "%${query}%"])// [title: "%${query}%"])
		if (list == miss){
			new Notification("ขออภัยครับ",
        		"ไม่พบรายวิชาที่ต้องการ กรุณาตรวจสอบใหม่อีกครั้ง",
        		Notification.Type.WARNING_MESSAGE).show(Page.getCurrent())
		}
		table.removeAllItems()
		for(la in list){
			Object id = table.addItem();
			table.getContainerProperty(id, "รหัสประจำตัว").setValue(new String(" ${la.student.name}"));
			table.getContainerProperty(id, "ชื่อวิชา").setValue(new String(" ${la.subject.subjectName}"));
			table.getContainerProperty(id, "รายการ").setValue(new String(" ${la.type}"));
			}
	}
	public void loadAllLabOrder(){
		def enroll = Enrollment.executeQuery("From Enrollment")
		int i = 0
		for(e in enroll){
			Object id = table.addItem();
			table.getContainerProperty(id, "รหัสประจำตัว").setValue(new String(" ${e.student.name}"));
			table.getContainerProperty(id, "ชื่อวิชา").setValue(new String(" ${e.subject.subjectName}"));
			table.getContainerProperty(id, "รายการ").setValue(new String(" ${e.type}"));
		}
	
	}
}
