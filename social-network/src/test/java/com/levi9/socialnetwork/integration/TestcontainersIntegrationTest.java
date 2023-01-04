package com.levi9.socialnetwork.integration;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.ClassRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.jdbc.JdbcDatabaseDelegate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.levi9.socialnetwork.Controller.EventController;
import com.levi9.socialnetwork.Exception.CustomExceptionHandler;
import com.levi9.socialnetwork.Model.Event;
import com.levi9.socialnetwork.Service.impl.EventServiceImpl;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Import(CustomExceptionHandler.class)
@EnableWebSecurity

class TestcontainersIntegrationTest {

	@InjectMocks
	private EventController eventController;

	@MockBean
	private EventServiceImpl eventService;

	MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	static final Long eventId = 1L;

	@BeforeEach
	public void setup() throws Exception {

		MockitoAnnotations.initMocks(this);

		this.mockMvc = MockMvcBuilders.standaloneSetup(eventController).build();

	}

	public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

		@ClassRule
		public static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:11.1")
				.withDatabaseName("integration-tests-db").withUsername("postgres").withPassword("postgres");

		static {
			postgreSQLContainer.start();

			var containerDelegate = new JdbcDatabaseDelegate(postgreSQLContainer, "");

//		     ScriptUtils.runInitScript(containerDelegate, "some/location/on/classpath/someScriptFirst.sql");
		}

		@Override
		public void initialize(ConfigurableApplicationContext applicationContext) {

			TestPropertyValues
					.of("spring.jpa.hibernate.ddl-auto=none", "spring.datasource.initialization-mode=always",
							"spring.datasource.platform=postgresql",
							"spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation=true",
							"spring.datasource.driver-class-name=org.testcontainers.jdbc.ContainerDatabaseDriver",
							"spring.datasource.url=jdbc:postgresql://localhost:5432/socialnetwork",
							"spring.datasource.username=postgres", "spring.datasource.password=postgres",
							"spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQL95Dialect",
							"spring.jpa.properties.hibernate.enable_lazy_load_no_trans=true",
							"spring.liquibase.change-log=classpath:/dblogs/changelog/changelog-master.xml",
							"spring.liquibase.contexts=test", "logging.level.liquibase=INFO")
					.applyTo(applicationContext);
		}
	}

	@Test
	void itShouldReturnAllEvents() throws Exception {

		List<Event> listOfEvents = new ArrayList<>();
		listOfEvents.add(Event.builder().id(1L).userId(1l).groupId(1L).build());
		listOfEvents.add(Event.builder().id(2L).userId(1l).groupId(1L).build());
		given(eventService.getAllEvents()).willReturn(listOfEvents);

		ResultActions response = mockMvc.perform(get("/api/events"));

		response.andExpect(status().isOk()).andDo(print()).andExpect(jsonPath("$.size()", is(listOfEvents.size())));
	}

}
