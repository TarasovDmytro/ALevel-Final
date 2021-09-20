package school.data.generator;

import com.vaadin.flow.spring.annotation.SpringComponent;

import school.data.service.MarkRepository;
import school.data.entity.Mark;
import school.data.service.SubjectRepository;
import school.data.entity.Subject;
import school.data.service.TeacherRepository;
import school.data.entity.Teacher;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import com.vaadin.exampledata.DataType;
import com.vaadin.exampledata.ExampleDataGenerator;

@SpringComponent
public class DataGenerator {

    @Bean
    public CommandLineRunner loadData(MarkRepository markRepository, SubjectRepository subjectRepository,
            TeacherRepository teacherRepository) {
        return args -> {
            Logger logger = LoggerFactory.getLogger(getClass());
            if (markRepository.count() != 0L) {
                logger.info("Using existing database");
                return;
            }
            int seed = 123;

            logger.info("Generating demo data");

            logger.info("... generating 100 Mark entities...");
            ExampleDataGenerator<Mark> markRepositoryGenerator = new ExampleDataGenerator<>(Mark.class,
                    LocalDateTime.of(2021, 9, 20, 0, 0, 0));
            markRepositoryGenerator.setData(Mark::setId, DataType.ID);
            markRepositoryGenerator.setData(Mark::setId, DataType.NUMBER_UP_TO_10);
            markRepositoryGenerator.setData(Mark::setSubjectName, DataType.WORD);
            markRepositoryGenerator.setData(Mark::setStudentName, DataType.FULL_NAME);
            markRepositoryGenerator.setData(Mark::setDate, DataType.DATE_LAST_7_DAYS);
            markRepositoryGenerator.setData(Mark::setValue, DataType.NUMBER_UP_TO_10);
            markRepository.saveAll(markRepositoryGenerator.create(100, seed));

            logger.info("... generating 100 Subject entities...");
            ExampleDataGenerator<Subject> subjectRepositoryGenerator = new ExampleDataGenerator<>(Subject.class,
                    LocalDateTime.of(2021, 9, 20, 0, 0, 0));
            subjectRepositoryGenerator.setData(Subject::setId, DataType.ID);
            subjectRepositoryGenerator.setData(Subject::setId, DataType.NUMBER_UP_TO_10);
            subjectRepositoryGenerator.setData(Subject::setName, DataType.WORD);
            subjectRepositoryGenerator.setData(Subject::setTeacherName, DataType.FULL_NAME);
            subjectRepository.saveAll(subjectRepositoryGenerator.create(100, seed));

            logger.info("... generating 100 Teacher entities...");
            ExampleDataGenerator<Teacher> teacherRepositoryGenerator = new ExampleDataGenerator<>(Teacher.class,
                    LocalDateTime.of(2021, 9, 20, 0, 0, 0));
            teacherRepositoryGenerator.setData(Teacher::setId, DataType.ID);
            teacherRepositoryGenerator.setData(Teacher::setId, DataType.NUMBER_UP_TO_100);
            teacherRepositoryGenerator.setData(Teacher::setFullName, DataType.FULL_NAME);
            teacherRepositoryGenerator.setData(Teacher::setSubjectName, DataType.WORD);
            teacherRepository.saveAll(teacherRepositoryGenerator.create(100, seed));

            logger.info("Generated demo data");
        };
    }

}