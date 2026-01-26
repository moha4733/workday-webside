package dk.tommer.workday.repository;

import dk.tommer.workday.entity.User;
import dk.tommer.workday.entity.WorkLog;
import dk.tommer.workday.entity.WorkLogStatus;
import dk.tommer.workday.entity.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class WorkLogRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private WorkLogRepository workLogRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    private User testUser;
    private Project testProject;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = new User();
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser.setRole(dk.tommer.workday.entity.Role.SVEND);
        testUser = userRepository.save(testUser);

        // Create test project
        testProject = new Project();
        testProject.setName("Test Project");
        testProject.setDescription("Test Description");
        testProject.setStatus(dk.tommer.workday.entity.ProjectStatus.PLANNED);
        testProject = projectRepository.save(testProject);
    }

    @Test
    void findByUserOrderByDateDesc_returnsWorkLogsInDescendingOrder() {
        // Create work logs with different dates
        WorkLog workLog1 = createWorkLog(LocalDate.of(2024, 1, 10), 8.0);
        WorkLog workLog2 = createWorkLog(LocalDate.of(2024, 1, 15), 6.5);
        WorkLog workLog3 = createWorkLog(LocalDate.of(2024, 1, 5), 7.5);

        workLogRepository.save(workLog1);
        workLogRepository.save(workLog2);
        workLogRepository.save(workLog3);

        List<WorkLog> workLogs = workLogRepository.findByUserOrderByDateDesc(testUser);

        assertThat(workLogs).hasSize(3);
        assertThat(workLogs.get(0).getDate()).isEqualTo(LocalDate.of(2024, 1, 15));
        assertThat(workLogs.get(1).getDate()).isEqualTo(LocalDate.of(2024, 1, 10));
        assertThat(workLogs.get(2).getDate()).isEqualTo(LocalDate.of(2024, 1, 5));
    }

    @Test
    void findByStatus_returnsWorkLogsWithGivenStatus() {
        WorkLog approvedWorkLog = createWorkLog(LocalDate.of(2024, 1, 10), 8.0);
        approvedWorkLog.setStatus(WorkLogStatus.APPROVED);
        
        WorkLog pendingWorkLog = createWorkLog(LocalDate.of(2024, 1, 11), 6.5);
        pendingWorkLog.setStatus(WorkLogStatus.PENDING);

        workLogRepository.save(approvedWorkLog);
        workLogRepository.save(pendingWorkLog);

        List<WorkLog> approvedLogs = workLogRepository.findByStatus(WorkLogStatus.APPROVED);
        List<WorkLog> pendingLogs = workLogRepository.findByStatus(WorkLogStatus.PENDING);

        assertThat(approvedLogs).hasSize(1);
        assertThat(approvedLogs.get(0).getStatus()).isEqualTo(WorkLogStatus.APPROVED);

        assertThat(pendingLogs).hasSize(1);
        assertThat(pendingLogs.get(0).getStatus()).isEqualTo(WorkLogStatus.PENDING);
    }

    @Test
    void findByUser_IdAndStatusAndDateBetweenOrderByDateAsc_returnsFilteredWorkLogs() {
        // Create work logs in date range
        WorkLog workLog1 = createWorkLog(LocalDate.of(2024, 1, 10), 8.0);
        workLog1.setStatus(WorkLogStatus.APPROVED);
        workLogRepository.save(workLog1);
        
        WorkLog workLog2 = createWorkLog(LocalDate.of(2024, 1, 15), 6.5);
        workLog2.setStatus(WorkLogStatus.APPROVED);
        workLogRepository.save(workLog2);
        
        // Create work log outside date range
        WorkLog workLog3 = createWorkLog(LocalDate.of(2024, 2, 1), 7.5);
        workLog3.setStatus(WorkLogStatus.REJECTED);
        workLogRepository.save(workLog3);

        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);

        List<WorkLog> workLogs = workLogRepository.findByUser_IdAndStatusAndDateBetweenOrderByDateAsc(
            testUser.getId(), WorkLogStatus.APPROVED, startDate, endDate);

        assertThat(workLogs).hasSize(2);
        assertThat(workLogs).extracting(WorkLog::getDate).containsExactly(
            LocalDate.of(2024, 1, 10), LocalDate.of(2024, 1, 15));
        assertThat(workLogs).allMatch(wl -> wl.getStatus() == WorkLogStatus.APPROVED);
    }

    @Test
    void findByStatusAndDateBetweenOrderByDateAsc_returnsFilteredWorkLogs() {
        // Create work logs with different statuses and dates
        WorkLog approved1 = createWorkLog(LocalDate.of(2024, 1, 10), 8.0);
        approved1.setStatus(WorkLogStatus.APPROVED);
        
        WorkLog approved2 = createWorkLog(LocalDate.of(2024, 1, 15), 6.5);
        approved2.setStatus(WorkLogStatus.APPROVED);
        
        WorkLog rejected = createWorkLog(LocalDate.of(2024, 1, 12), 7.5);
        rejected.setStatus(WorkLogStatus.REJECTED);

        workLogRepository.save(approved1);
        workLogRepository.save(approved2);
        workLogRepository.save(rejected);

        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);

        List<WorkLog> approvedLogs = workLogRepository.findByStatusAndDateBetweenOrderByDateAsc(
            WorkLogStatus.APPROVED, startDate, endDate);

        assertThat(approvedLogs).hasSize(2);
        assertThat(approvedLogs).extracting(WorkLog::getDate).containsExactly(
            LocalDate.of(2024, 1, 10), LocalDate.of(2024, 1, 15));
        assertThat(approvedLogs).allMatch(wl -> wl.getStatus() == WorkLogStatus.APPROVED);
    }

    @Test
    void sumHoursByUserAndDateRange_returnsTotalHours() {
        WorkLog workLog1 = createWorkLog(LocalDate.of(2024, 1, 10), 8.0);
        workLog1.setStatus(WorkLogStatus.APPROVED);
        workLogRepository.save(workLog1);
        
        WorkLog workLog2 = createWorkLog(LocalDate.of(2024, 1, 15), 6.5);
        workLog2.setStatus(WorkLogStatus.APPROVED);
        workLogRepository.save(workLog2);
        
        WorkLog workLog3 = createWorkLog(LocalDate.of(2024, 1, 20), 7.5);
        workLog3.setStatus(WorkLogStatus.APPROVED);
        workLogRepository.save(workLog3);

        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);

        Double totalHours = workLogRepository.sumHoursByUserAndDateRange(testUser.getId(), startDate, endDate);

        assertThat(totalHours).isEqualTo(22.0);
    }

    @Test
    void sumHoursByUserAndDateRange_noWorkLogs_returnsZero() {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);

        Double totalHours = workLogRepository.sumHoursByUserAndDateRange(testUser.getId(), startDate, endDate);

        assertThat(totalHours).isEqualTo(0.0);
    }

    @Test
    void save_validWorkLog_savesWorkLog() {
        WorkLog workLog = createWorkLog(LocalDate.of(2024, 1, 10), 8.0);

        WorkLog saved = workLogRepository.save(workLog);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUser()).isEqualTo(testUser);
        assertThat(saved.getProject()).isEqualTo(testProject);
        assertThat(saved.getHours()).isEqualTo(8.0);
        assertThat(saved.getDate()).isEqualTo(LocalDate.of(2024, 1, 10));
    }

    @Test
    void deleteById_existingWorkLog_deletesWorkLog() {
        WorkLog workLog = createWorkLog(LocalDate.of(2024, 1, 10), 8.0);
        WorkLog saved = workLogRepository.save(workLog);

        workLogRepository.deleteById(saved.getId());

        assertThat(workLogRepository.findById(saved.getId())).isEmpty();
    }

    private WorkLog createWorkLog(LocalDate date, double hours) {
        WorkLog workLog = new WorkLog();
        workLog.setUser(testUser);
        workLog.setProject(testProject);
        workLog.setDate(date);
        workLog.setHours(hours);
        workLog.setStatus(WorkLogStatus.PENDING);
        workLog.setAdminComment("Test comment");
        return workLog;
    }
}
