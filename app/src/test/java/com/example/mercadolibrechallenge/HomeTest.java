package com.example.mercadolibrechallenge;

import android.widget.Toast;

import com.example.mercadolibrechallenge.helpers.NavigationHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Toast.class, NavigationHelper.class})
public class HomeTest {
    private Home home = new Home();
    private Toast toast = Mockito.mock(Toast.class);

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(NavigationHelper.class);
        PowerMockito.doNothing().when(NavigationHelper.class, "navigateTo", home, SearchList.class, "","", 10, 0, 1);

        PowerMockito.mockStatic(Toast.class);
        PowerMockito.when(Toast.makeText(home, "Debe agregar un texto de búsqueda", Toast.LENGTH_LONG)).thenReturn(toast);
    }

    @Test
    public void emptySearchString() {
        home.search("");

        PowerMockito.verifyStatic(Toast.class, VerificationModeFactory.times(1));
        Toast.makeText(home, "Debe agregar un texto de búsqueda", Toast.LENGTH_LONG);

        PowerMockito.verifyStatic(NavigationHelper.class, VerificationModeFactory.times(0));
        NavigationHelper.navigateTo(home, SearchList.class, "","", 10, 0, 1);
    }

    @Test
    public void searchString(){
        home.search("abc");

        PowerMockito.verifyStatic(NavigationHelper.class);
        NavigationHelper.navigateTo(home, SearchList.class, "","abc", 10, 0, 1);

        PowerMockito.verifyStatic(Toast.class, VerificationModeFactory.times(0));
        Toast.makeText(home, "Debe agregar un texto de búsqueda", Toast.LENGTH_LONG);
    }
}
