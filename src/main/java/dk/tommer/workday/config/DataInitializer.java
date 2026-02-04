package dk.tommer.workday.config;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import dk.tommer.workday.entity.DayPlan;
import dk.tommer.workday.entity.MaterialOrder;
import dk.tommer.workday.entity.MaterialStatus;
import dk.tommer.workday.entity.Project;
import dk.tommer.workday.entity.ProjectPriority;
import dk.tommer.workday.entity.ProjectStatus;
import dk.tommer.workday.entity.Role;
import dk.tommer.workday.entity.User;
import dk.tommer.workday.entity.WorkLog;
import dk.tommer.workday.entity.WorkType;
import dk.tommer.workday.repository.DayPlanRepository;
import dk.tommer.workday.repository.MaterialOrderRepository;
import dk.tommer.workday.repository.ProjectRepository;
import dk.tommer.workday.repository.UserRepository;
import dk.tommer.workday.repository.WorkLogRepository;
import dk.tommer.workday.repository.WorkTypeRepository;
import dk.tommer.workday.repository.CompanyRepository;
import dk.tommer.workday.entity.Company;

@Component
@Profile("!test") // Kør ikke i test-profil
public class DataInitializer implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private MaterialOrderRepository materialOrderRepository;

    @Autowired
    private WorkLogRepository workLogRepository;

    @Autowired
    private DayPlanRepository dayPlanRepository;

    @Autowired
    private WorkTypeRepository workTypeRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @org.springframework.beans.factory.annotation.Value("${admin.email:admin@workday.dk}")
    private String adminEmail;

    @org.springframework.beans.factory.annotation.Value("${admin.password:admin123}")
    private String adminPassword;

    @org.springframework.beans.factory.annotation.Value("${svend.email:svend@workday.dk}")
    private String svendEmail;

    @org.springframework.beans.factory.annotation.Value("${svend.password:svend123}")
    private String svendPassword;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        try {
            logger.info("DataInitializer started - checking for admin user...");

            // Check if admin user already exists
            if (userRepository.findByEmail(adminEmail).isEmpty()) {
                logger.info("Admin user not found. Creating default admin user...");
                
                User admin = new User();
                admin.setName("System Admin");
                admin.setEmail(adminEmail);
                admin.setPassword(passwordEncoder.encode(adminPassword));
                admin.setRole(Role.ADMIN);
                
                User savedUser = userRepository.save(admin);
                logger.info("=== Admin user created successfully ===");
                logger.info("ID: {}", savedUser.getId());
                logger.info("Email: {}", adminEmail);
                logger.info("Password: {}", adminPassword);
                logger.info("Role: {}", savedUser.getRole());
            } else {
                // Update existing admin user to ensure it has the correct role and password
                User existingAdmin = userRepository.findByEmail(adminEmail).get();
                logger.info("Admin user found with ID: {} and role: {}", existingAdmin.getId(), existingAdmin.getRole());
                
                if (existingAdmin.getRole() != Role.ADMIN) {
                    logger.info("Updating existing user to admin role...");
                    existingAdmin.setRole(Role.ADMIN);
                }
                
                // Always update password to ensure it's correctly encoded
                logger.info("Updating admin password to ensure correct encoding...");
                existingAdmin.setPassword(passwordEncoder.encode(adminPassword));
                
                userRepository.save(existingAdmin);
                logger.info("Admin user updated with correct role and password");
            }
            logger.info("DataInitializer completed successfully");
            
            // Opret eller hent Svend bruger
            User svend = userRepository.findByEmail(svendEmail).orElse(null);
            if (svend == null) {
                svend = new User();
                svend.setName("Svend Bruger");
                svend.setEmail(svendEmail);
                svend.setPassword(passwordEncoder.encode(svendPassword));
                svend.setRole(Role.SVEND);
                svend = userRepository.save(svend);
                logger.info("Svend user created: {} / {}", svendEmail, svendPassword);
            } else {
                // Opdater password hvis nødvendigt
                svend.setPassword(passwordEncoder.encode(svendPassword));
                svend.setRole(Role.SVEND);
                svend = userRepository.save(svend);
            }

            // Opret test data kun hvis der ikke allerede er projekter
            if (projectRepository.count() == 0) {
                logger.info("Creating test data for Svend...");
                
                // Sørg for at der er et firma til stede
                if (companyRepository.count() == 0) {
                    Company company = new Company();
                    company.setCompanyName("Tommer Entreprise");
                    company.setCvrNumber("12345678");
                    company.setStandardHourlyRate(210.0);
                    companyRepository.save(company);
                    logger.info("Default company created");
                }
                
                createTestData(svend);
                logger.info("Test data created successfully");
            }

            // Appen bruger kun ADMIN + SVEND. Sørg for at alle ikke-admin brugere har SVEND-rolle,
            // så ældre data (USER/null) ikke giver uventet flow efter login.
            userRepository.findAll().stream()
                    .filter(u -> u.getEmail() != null && !adminEmail.equalsIgnoreCase(u.getEmail()))
                    .filter(u -> u.getRole() == null || u.getRole() == Role.USER)
                    .forEach(u -> {
                        logger.info("Upgrading user role to SVEND for: {} (was: {})", u.getEmail(), u.getRole());
                        u.setRole(Role.SVEND);
                        userRepository.save(u);
                    });
        } catch (Exception e) {
            logger.error("Error in DataInitializer: {}", e.getMessage(), e);
            throw e;
        }
    }

    private void createTestData(User svend) {
        // Opret arbejdstyper
        WorkType renovation = createOrGetWorkType("Renovering", "Renovering af eksisterende bygninger");
        WorkType maintenance = createOrGetWorkType("Vedligeholdelse", "Vedligeholdelse og reparationer");

        // Opret projekter
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        LocalDate nextWeek = today.plusDays(7);

        Project project1 = new Project();
        project1.setName("Renovering af køkken - Vestergade 12");
        project1.setDescription("Fuldt køkkenrenovering inkl. nye skabe, bordplade og el-installationer");
        project1.setAddress("Vestergade 12, 8000 Aarhus C");
        project1.setStartDate(tomorrow);
        project1.setStartTime(LocalTime.of(7, 0));
        project1.setStatus(ProjectStatus.PLANNED);
        project1.setPriority(ProjectPriority.HIGH);
        project1.setAssignedUser(svend);
        project1.setWorkType(renovation);
        project1 = projectRepository.save(project1);

        Project project2 = new Project();
        project2.setName("Gulvlægning - Nørregade 45");
        project2.setDescription("Lægning af nyt trægulv i stue og soveværelse");
        project2.setAddress("Nørregade 45, 8000 Aarhus C");
        project2.setStartDate(nextWeek);
        project2.setStartTime(LocalTime.of(8, 0));
        project2.setStatus(ProjectStatus.IN_PROGRESS);
        project2.setPriority(ProjectPriority.MEDIUM);
        project2.setAssignedUser(svend);
        project2.setWorkType(renovation);
        project2 = projectRepository.save(project2);

        Project project3 = new Project();
        project3.setName("Vinduesudskiftning - Hovedgaden 7");
        project3.setDescription("Udskiftning af 8 vinduer med energivenlige modeller");
        project3.setAddress("Hovedgaden 7, 8200 Aarhus N");
        project3.setStartDate(today.plusDays(3));
        project3.setStartTime(LocalTime.of(7, 30));
        project3.setStatus(ProjectStatus.PLANNED);
        project3.setPriority(ProjectPriority.LOW);
        project3.setAssignedUser(svend);
        project3.setWorkType(maintenance);
        project3 = projectRepository.save(project3);

        // Opret bestillinger
        MaterialOrder order1 = new MaterialOrder();
        order1.setUser(svend);
        order1.setProject(project1);
        order1.setStatus(MaterialStatus.PENDING);
        order1.setDescription("Gulvmateriale bestilling: 25.5 m2 - Køkkenrenovering");
        order1.setCreatedAt(LocalDateTime.now().minusDays(2));
        materialOrderRepository.save(order1);

        MaterialOrder order2 = new MaterialOrder();
        order2.setUser(svend);
        order2.setProject(project2);
        order2.setStatus(MaterialStatus.APPROVED);
        order2.setDescription("Trægulv: 40 m2 egetræ - Nørregade projekt");
        order2.setCreatedAt(LocalDateTime.now().minusDays(5));
        materialOrderRepository.save(order2);

        // Opret timer (work logs) for de sidste dage
        WorkLog log1 = new WorkLog();
        log1.setUser(svend);
        log1.setProject(project2);
        log1.setDate(today.minusDays(1));
        log1.setHours(7.5);
        workLogRepository.save(log1);

        WorkLog log2 = new WorkLog();
        log2.setUser(svend);
        log2.setProject(project2);
        log2.setDate(today.minusDays(2));
        log2.setHours(8.0);
        workLogRepository.save(log2);

        WorkLog log3 = new WorkLog();
        log3.setUser(svend);
        log3.setProject(project3);
        log3.setDate(today.minusDays(3));
        log3.setHours(6.0);
        workLogRepository.save(log3);

        // Opret dagplaner for de næste 5 dage
        Project[] projectsForDays = {project2, project1, project1, project3, project2};
        String[] dayDescriptions = {"I dag - Nørregade", "I morgen - Vestergade", 
                                     "Overmorgen - Vestergade", "Dag 4 - Hovedgaden", "Dag 5 - Nørregade"};
        
        for (int i = 0; i < 5; i++) {
            LocalDate planDate = today.plusDays(i);
            DayPlan dayPlan = new DayPlan();
            dayPlan.setUser(svend);
            dayPlan.setProject(projectsForDays[i]);
            dayPlan.setDate(planDate);
            dayPlanRepository.save(dayPlan);
            logger.debug("Created day plan for {}: {}", planDate, dayDescriptions[i]);
        }

        logger.info("Created test data: 3 projects, 2 material orders, 3 work logs, 5 day plans");
    }

    private WorkType createOrGetWorkType(String name, String description) {
        // Tjek om work type allerede eksisterer
        List<WorkType> existing = workTypeRepository.findAllByOrderByNameAsc();
        return existing.stream()
                .filter(wt -> wt.getName().equals(name))
                .findFirst()
                .orElseGet(() -> {
                    WorkType wt = new WorkType(name, description);
                    return workTypeRepository.save(wt);
                });
    }
}
