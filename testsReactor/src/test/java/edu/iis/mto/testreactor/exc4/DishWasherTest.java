package edu.iis.mto.testreactor.exc4;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

public class DishWasherTest {

    WaterPump waterPump;
    Engine engine;
    DirtFilter dirtFilter;
    Door hodor;

    DishWasher dishWasher;
    ProgramConfiguration programConfiguration;

    @Before
    public void setUp(){
        waterPump = Mockito.mock(WaterPump.class);
        engine = Mockito.mock(Engine.class);
        dirtFilter = Mockito.mock(DirtFilter.class);
        hodor = Mockito.mock(Door.class);

        dishWasher = new DishWasher(waterPump, engine, dirtFilter, hodor);
        programConfiguration = ProgramConfiguration.builder()
                                                   .withProgram(WashingProgram.RINSE)
                                                   .withTabletsUsed(true)
                                                   .build();
    }

    @Test
    public void shouldReturnDoorOpenErrorWhenDoorAreClosed(){
        when(hodor.closed()).thenReturn(true);

        assertThat(dishWasher.start(programConfiguration).getStatus(), is(Status.DOOR_OPEN_ERROR));
    }

    @Test
    public void dishWasherShouldNotRunIfFilterIsNotClean(){
        when(hodor.closed()).thenReturn(false);
        when(dirtFilter.capacity()).thenReturn(49.0d);

        assertThat(dishWasher.start(programConfiguration).getStatus(), is(Status.ERROR_FILTER));
    }

    @Test
    public void engineAndWaterPumpShouldCallMethodsOnce(){
        when(hodor.closed()).thenReturn(false);
        when(dirtFilter.capacity()).thenReturn(60.0d);

        dishWasher.start(programConfiguration);

        try {
            verify(waterPump, times(1)).pour(programConfiguration.getProgram());
            verify(engine, times(1)).runProgram(anyInt());
            verify(waterPump, times(1)).drain();
        } catch (PumpException e) {
            fail();
        } catch (EngineException e) {
            fail();
        }
    }

    @Test
    public void engineShouldCallRunProgramTwiceWhenWashingProgramIsNotRinse(){
        programConfiguration = ProgramConfiguration.builder()
                                                   .withProgram(WashingProgram.INTENSIVE)
                                                   .withTabletsUsed(true)
                                                   .build();

        when(hodor.closed()).thenReturn(false);
        when(dirtFilter.capacity()).thenReturn(60.0d);

        dishWasher.start(programConfiguration);

        try {
            verify(engine, times(2)).runProgram(anyInt());
        } catch (EngineException e) {
            fail();
        }
    }

    @Test
    public void shouldReturnStatusErrorPumpOnThrowPumpException(){
        programConfiguration = ProgramConfiguration.builder()
                                                   .withProgram(WashingProgram.RINSE)
                                                   .withTabletsUsed(true)
                                                   .build();

        when(hodor.closed()).thenReturn(false);
        when(dirtFilter.capacity()).thenReturn(60.0d);
        when(dishWasher.start(programConfiguration)).thenThrow(new PumpException());

        assertThat(dishWasher.start(programConfiguration).getStatus(), is(Status.ERROR_PUMP));
    }

    @Test
    public void test() {
        fail("Not yet implemented");
    }

}
