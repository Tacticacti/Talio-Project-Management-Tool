package server;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;

import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;


import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
class MainTest {
    @MockBean
    private Admin admin;


    @Test
    void testAdmin(){
        when(admin.getPassword()).thenReturn("password");

        String[] args = {};
        Main.main(args);

        verify(admin).getPassword();
    }
}
