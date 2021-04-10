package chim.logic.commands;

import static chim.commons.core.Messages.MESSAGE_CUSTOMERS_FOUND_OVERVIEW;
import static chim.logic.commands.CommandTestUtil.assertCommandSuccess;
import static chim.testutil.TypicalCustomers.CARL;
import static chim.testutil.TypicalCustomers.ELLE;
import static chim.testutil.TypicalCustomers.FIONA;
import static chim.testutil.TypicalModels.getTypicalChim;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import chim.model.Model;
import chim.model.ModelManager;
import chim.model.UserPrefs;
import chim.model.customer.Customer;
import chim.model.customer.predicates.CustomerNamePredicate;
import chim.model.util.predicate.CompositeFieldPredicate;

/**
 * Contains integration tests (interaction with the Model) for {@code FindCommand}.
 */
public class FindCustomerCommandTest {
    private Model model = new ModelManager(getTypicalChim(), new UserPrefs());
    private Model expectedModel = new ModelManager(getTypicalChim(), new UserPrefs());

    @Test
    public void equals() {
        final List<String> firstList = Collections.singletonList("first");
        final List<String> secondList = Collections.singletonList("second");

        CompositeFieldPredicate<Customer> firstPredicate =
                new CompositeFieldPredicate<>(new CustomerNamePredicate(firstList));
        CompositeFieldPredicate<Customer> secondPredicate =
                new CompositeFieldPredicate<>(new CustomerNamePredicate(secondList));

        FindCustomerCommand findFirstCommand = new FindCustomerCommand(firstPredicate);
        FindCustomerCommand findSecondCommand = new FindCustomerCommand(secondPredicate);

        // same object -> returns true
        assertTrue(findFirstCommand.equals(findFirstCommand));

        // same values -> returns true
        FindCustomerCommand findFirstCommandCopy = new FindCustomerCommand(firstPredicate);
        assertTrue(findFirstCommand.equals(findFirstCommandCopy));

        // different types -> returns false
        assertFalse(findFirstCommand.equals(1));

        // null -> returns false
        assertFalse(findFirstCommand.equals(null));

        // different customer -> returns false
        assertFalse(findFirstCommand.equals(findSecondCommand));
    }

    @Test
    public void execute_multipleKeywords_multipleCustomersFound() {
        final String keywordsString = "Kurz Elle Kunz";
        CompositeFieldPredicate<Customer> predicate = preparePredicate(keywordsString);
        String expectedMessage = String.format(MESSAGE_CUSTOMERS_FOUND_OVERVIEW, 3, predicate);
        FindCustomerCommand command = new FindCustomerCommand(predicate);
        expectedModel.updateFilteredCustomerList(predicate);
        expectedModel.setPanelToCustomerList();
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Arrays.asList(CARL, ELLE, FIONA), model.getFilteredCustomerList());
    }

    /**
     * Parses {@code userInput} into a {@code ModelCompositePredicate}.
     */
    private CompositeFieldPredicate<Customer> preparePredicate(String userInput) {
        return new CompositeFieldPredicate<>(new CustomerNamePredicate(Arrays.asList(userInput.split("\\s+"))));
    }

}