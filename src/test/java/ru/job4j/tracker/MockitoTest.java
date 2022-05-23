package ru.job4j.tracker;

import org.junit.Test;
import ru.job4j.tracker.action.DeleteAction;
import ru.job4j.tracker.action.FindByIdAction;
import ru.job4j.tracker.action.FindByNameAction;
import ru.job4j.tracker.action.ReplaceAction;
import ru.job4j.tracker.input.Input;
import ru.job4j.tracker.model.Item;
import ru.job4j.tracker.output.Output;
import ru.job4j.tracker.output.StubOutput;
import ru.job4j.tracker.store.MemTracker;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MockitoTest {

    @Test
    public void whenReplaceAction() {
        Output out = new StubOutput();
        Store tracker = new MemTracker();
        tracker.add(new Item("Replaced item"));
        String replacedName = "New item name";
        ReplaceAction rep = new ReplaceAction(out);

        Input input = mock(Input.class);

        when(input.askInt(any(String.class))).thenReturn(0);
        when(input.askStr(any(String.class))).thenReturn(replacedName);

        rep.execute(input, tracker);
        String ln = System.lineSeparator();
        assertThat(out.toString(), is("=== Edit item ===" + ln + "Item is successfully replaced!" + ln));
        assertThat(tracker.findAll().get(0).getName(), is(replacedName));
    }

    @Test
    public void whenDeleteAction() {
        Output out = new StubOutput();
        Store tracker = new MemTracker();
        tracker.add(new Item("Deleted item"));
        DeleteAction del = new DeleteAction(out);

        Input input = mock(Input.class);

        when(input.askInt(any(String.class))).thenReturn(0);

        del.execute(input, tracker);
        String ln = System.lineSeparator();
        assertThat(out.toString(), is("=== Delete item ===" + ln + "Item is successfully deleted!" + ln));
        assertThat(tracker.findAll().size(), is(0));
    }

    @Test
    public void whenFindByIdAction() {
        Output out = new StubOutput();
        Store tracker = new MemTracker();
        tracker.add(new Item("Just some item"));
        FindByIdAction findById = new FindByIdAction(out);

        Input input = mock(Input.class);

        when(input.askInt(any(String.class))).thenReturn(0);

        findById.execute(input, tracker);
        String ln = System.lineSeparator();
        assertThat(out.toString(), is("=== Find item by Id ===" + ln + tracker.findAll().get(0).toString() + ln));
    }

    @Test
    public void whenFindByNameAction() {
        Output out = new StubOutput();
        Store tracker = new MemTracker();
        String itemName = "Just some item";
        tracker.add(new Item(itemName));
        FindByNameAction findByName = new FindByNameAction(out);

        Input input = mock(Input.class);

        when(input.askStr(any(String.class))).thenReturn(itemName);

        findByName.execute(input, tracker);
        String ln = System.lineSeparator();
        assertThat(out.toString(), is("=== Find item by name ===" + ln + tracker.findAll().get(0).toString() + ln));
    }
}
