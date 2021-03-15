package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.showOrderAtIndex;
import static seedu.address.model.AbstractDate.TO_JSON_STRING_FORMATTER;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_ORDER;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_ORDER;
import static seedu.address.testutil.TypicalIndexes.INDEX_COMPLETED_ORDER;
import static seedu.address.testutil.TypicalIndexes.INDEX_UNCOMPLETED_ORDER;
import static seedu.address.testutil.TypicalModels.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.order.Order;
import seedu.address.testutil.OrderBuilder;
import java.time.LocalDateTime;

public class DoneCommandTest {
    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validIndexUnfilteredList_success() {
        Order order = model.getFilteredOrderList().get(INDEX_UNCOMPLETED_ORDER.getZeroBased());
        Order updatedOrder = new OrderBuilder(order)
                .withCompletedDate(LocalDateTime.now().format(TO_JSON_STRING_FORMATTER))
                .build();
        DoneCommand doneCommand = new DoneCommand(INDEX_UNCOMPLETED_ORDER);

        String expectedMessage = String.format(DoneCommand.MESSAGE_MARK_ORDER_DONE_SUCCESS,
                updatedOrder);

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setOrder(order , updatedOrder);
        assertCommandSuccess(doneCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidOrderIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredOrderList().size() + 1);
        DoneCommand doneCommand = new DoneCommand(outOfBoundIndex);

        assertCommandFailure(doneCommand, model, Messages.MESSAGE_INVALID_ORDER_INDEX);
    }

    @Test
    public void execute_validIndexfilteredList_success() {
        showOrderAtIndex(model, INDEX_UNCOMPLETED_ORDER);
        Order order = model.getFilteredOrderList().get(INDEX_FIRST_ORDER.getZeroBased());
        Order updatedOrder = new OrderBuilder(order)
                .withCompletedDate(LocalDateTime.now().format(TO_JSON_STRING_FORMATTER))
                .build();
        DoneCommand doneCommand = new DoneCommand(INDEX_FIRST_ORDER);

        String expectedMessage = String.format(DoneCommand.MESSAGE_MARK_ORDER_DONE_SUCCESS,
                updatedOrder);

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setOrder(order , updatedOrder);
        assertCommandSuccess(doneCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidOrderIndexfilteredList_failure() {
        showOrderAtIndex(model, INDEX_FIRST_ORDER);

        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredOrderList().size() + 1);
        DoneCommand doneCommand = new DoneCommand(outOfBoundIndex);

        assertCommandFailure(doneCommand, model, Messages.MESSAGE_INVALID_ORDER_INDEX);
    }

    @Test
    public void execute_completedOrderUnfilteredList_failure() {
        DoneCommand doneCommand = new DoneCommand(INDEX_COMPLETED_ORDER);

        assertCommandFailure(doneCommand, model, Messages.MESSAGE_INVALID_ORDER_COMPLETE);
    }

    @Test
    public void execute_completedOrderfilteredList_failure() {
        showOrderAtIndex(model, INDEX_COMPLETED_ORDER);
        DoneCommand doneCommand = new DoneCommand(INDEX_FIRST_ORDER);

        assertCommandFailure(doneCommand, model, Messages.MESSAGE_INVALID_ORDER_COMPLETE);
    }

    @Test
    public void equals() {
        final DoneCommand standardCommand = new DoneCommand(INDEX_FIRST_ORDER);

        // Same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // Same values -> returns true
        DoneCommand standardCommandCopy = new DoneCommand(INDEX_FIRST_ORDER);
        assertTrue(standardCommand.equals(standardCommandCopy));

        // Different types -> returns false
        assertFalse(standardCommand.equals("COMMAND"));

        // Null -> returns false
        assertFalse(standardCommand.equals(null));

        // different index -> returns false
        DoneCommand secondOrderCommand = new DoneCommand(INDEX_SECOND_ORDER);
        assertFalse(standardCommand.equals(secondOrderCommand));
    }
}
