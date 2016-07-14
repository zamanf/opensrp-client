package org.ei.opensrp.repository;

import org.ei.drishti.dto.Action;
import org.ei.opensrp.domain.Report;
import org.ei.opensrp.util.ActionBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(RobolectricTestRunner.class)
public class AllReportsTest {
    @Mock
    private ReportRepository repository;
    private AllReports allReports;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        allReports = new AllReports(repository);
    }

    @Test
    public void shouldDelegateActionToReportRepository() throws Exception {
        Action iudAction = ActionBuilder.actionForReport("IUD", "40");

        allReports.handleAction(iudAction);

        verify(repository).update(new Report("IUD", "40", "some-month-summary-json"));
    }
}
