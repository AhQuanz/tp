package chim.logic.commands;

import static chim.logic.commands.CommandTestUtil.DESC_AMY;
import static chim.logic.commands.CommandTestUtil.DESC_BOB;
import static chim.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static chim.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static chim.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static chim.logic.commands.CommandTestUtil.assertCommandFailure;
import static chim.logic.commands.CommandTestUtil.assertCommandSuccess;
import static chim.logic.commands.CommandTestUtil.showCustomerAtIndex;
import static chim.testutil.TypicalIndexes.INDEX_FIRST_CUSTOMER;
import static chim.testutil.TypicalIndexes.INDEX_SECOND_CUSTOMER;
import static chim.testutil.TypicalModels.getTypicalChim;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import chim.commons.core.Messages;
import chim.commons.core.index.Index;
import chim.model.Chim;
import chim.model.Model;
import chim.model.ModelManager;
import chim.model.UserPrefs;
import chim.model.customer.Customer;
import chim.testutil.CustomerBuilder;
import chim.testutil.EditCustomerDescriptorBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for EditCustomerCommand.
 */
public class EditCustomerCommandTest {

    private Model model = new ModelManager(getTypicalChim(), new UserPrefs());

    @Test
    public void execute_allFieldsSpecifiedUnfilteredList_success() {
        Customer editedCustomer = new CustomerBuilder().withId(model.getFilteredCustomerList().get(0).getId()).build();
        EditCustomerCommand.EditCustomerDescriptor descriptor =
            new EditCustomerDescriptorBuilder(editedCustomer).build();
        EditCustomerCommand editCommand = new EditCustomerCommand(INDEX_FIRST_CUSTOMER, descriptor);

        String expectedMessage =
            String.format(EditCustomerCommand.MESSAGE_EDIT_CUSTOMER_SUCCESS, editedCustomer);

        Model expectedModel = new ModelManager(new Chim(model.getChim()), new UserPrefs());
        expectedModel.setCustomer(model.getFilteredCustomerList().get(0), editedCustomer);
        expectedModel.setPanelToCustomerList();

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_someFieldsSpecifiedUnfilteredList_success() {
        Index indexLastCustomer = Index.fromOneBased(model.getFilteredCustomerList().size());
        Customer lastCustomer = model.getFilteredCustomerList().get(indexLastCustomer.getZeroBased());

        CustomerBuilder customerInListInList = new CustomerBuilder(lastCustomer);
        Customer editedCustomer = customerInListInList.withName(VALID_NAME_BOB).withPhone(VALID_PHONE_BOB)
                .withTags(VALID_TAG_HUSBAND).build();

        EditCustomerCommand.EditCustomerDescriptor descriptor =
            new EditCustomerDescriptorBuilder().withName(VALID_NAME_BOB)
                .withPhone(VALID_PHONE_BOB).withTags(VALID_TAG_HUSBAND).build();
        EditCustomerCommand editCommand = new EditCustomerCommand(indexLastCustomer, descriptor);

        String expectedMessage = String.format(EditCustomerCommand.MESSAGE_EDIT_CUSTOMER_SUCCESS, editedCustomer);

        Model expectedModel = new ModelManager(new Chim(model.getChim()), new UserPrefs());
        expectedModel.setCustomer(lastCustomer, editedCustomer);
        expectedModel.setPanelToCustomerList();

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_noFieldSpecifiedUnfilteredList_success() {
        EditCustomerCommand editCommand =
            new EditCustomerCommand(INDEX_FIRST_CUSTOMER, new EditCustomerCommand.EditCustomerDescriptor());
        Customer editedCustomer = model.getFilteredCustomerList().get(INDEX_FIRST_CUSTOMER.getZeroBased());

        String expectedMessage = String.format(EditCustomerCommand.MESSAGE_EDIT_CUSTOMER_SUCCESS, editedCustomer);

        Model expectedModel = new ModelManager(new Chim(model.getChim()), new UserPrefs());
        expectedModel.setPanelToCustomerList();

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_filteredList_success() {
        showCustomerAtIndex(model, INDEX_FIRST_CUSTOMER);

        Customer customerInFilteredList = model.getFilteredCustomerList().get(INDEX_FIRST_CUSTOMER.getZeroBased());
        Customer editedCustomer = new CustomerBuilder(customerInFilteredList).withName(VALID_NAME_BOB).build();
        EditCustomerCommand editCommand = new EditCustomerCommand(INDEX_FIRST_CUSTOMER,
                new EditCustomerDescriptorBuilder().withName(VALID_NAME_BOB).build());

        String expectedMessage = String.format(EditCustomerCommand.MESSAGE_EDIT_CUSTOMER_SUCCESS, editedCustomer);

        Model expectedModel = new ModelManager(new Chim(model.getChim()), new UserPrefs());
        expectedModel.setCustomer(model.getFilteredCustomerList().get(0), editedCustomer);
        expectedModel.setPanelToCustomerList();

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_duplicateCustomerUnfilteredList_failure() {
        Customer firstCustomer = model.getFilteredCustomerList().get(INDEX_FIRST_CUSTOMER.getZeroBased());
        EditCustomerCommand.EditCustomerDescriptor descriptor =
            new EditCustomerDescriptorBuilder(firstCustomer).build();
        EditCustomerCommand editCommand = new EditCustomerCommand(INDEX_SECOND_CUSTOMER, descriptor);

        assertCommandFailure(editCommand, model, EditCustomerCommand.MESSAGE_DUPLICATE_CUSTOMER);
    }

    @Test
    public void execute_duplicateCustomerFilteredList_failure() {
        showCustomerAtIndex(model, INDEX_FIRST_CUSTOMER);

        // edit customer in filtered list into a duplicate in CHIM
        Customer customerInList = model.getChim().getCustomerList().get(INDEX_SECOND_CUSTOMER.getZeroBased());
        EditCustomerCommand editCommand = new EditCustomerCommand(INDEX_FIRST_CUSTOMER,
                new EditCustomerDescriptorBuilder(customerInList).build());

        assertCommandFailure(editCommand, model, EditCustomerCommand.MESSAGE_DUPLICATE_CUSTOMER);
    }

    @Test
    public void execute_invalidCustomerIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredCustomerList().size() + 1);
        EditCustomerCommand.EditCustomerDescriptor descriptor =
                new EditCustomerDescriptorBuilder().withName(VALID_NAME_BOB).build();
        EditCustomerCommand editCommand = new EditCustomerCommand(outOfBoundIndex, descriptor);

        assertCommandFailure(editCommand, model, Messages.MESSAGE_INVALID_CUSTOMER_DISPLAYED_INDEX);
    }

    /**
     * Test case where index is larger than the size of filtered list, but smaller than the
     * number of customers in CHIM.
     */
    @Test
    public void execute_invalidCustomerIndexFilteredList_failure() {
        showCustomerAtIndex(model, INDEX_FIRST_CUSTOMER);
        Index outOfBoundIndex = INDEX_SECOND_CUSTOMER;
        // ensures that outOfBoundIndex is still in bounds of CHIM's full customers list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getChim().getCustomerList().size());

        EditCustomerCommand editCommand = new EditCustomerCommand(outOfBoundIndex,
                new EditCustomerDescriptorBuilder().withName(VALID_NAME_BOB).build());

        assertCommandFailure(editCommand, model, Messages.MESSAGE_INVALID_CUSTOMER_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        final EditCustomerCommand standardCommand = new EditCustomerCommand(INDEX_FIRST_CUSTOMER, DESC_AMY);

        // same values -> returns true
        EditCustomerCommand.EditCustomerDescriptor copyDescriptor =
            new EditCustomerCommand.EditCustomerDescriptor(DESC_AMY);
        EditCustomerCommand commandWithSameValues = new EditCustomerCommand(INDEX_FIRST_CUSTOMER, copyDescriptor);
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // different index -> returns false
        assertFalse(standardCommand.equals(new EditCustomerCommand(INDEX_SECOND_CUSTOMER, DESC_AMY)));

        // different descriptor -> returns false
        assertFalse(standardCommand.equals(new EditCustomerCommand(INDEX_FIRST_CUSTOMER, DESC_BOB)));
    }

}