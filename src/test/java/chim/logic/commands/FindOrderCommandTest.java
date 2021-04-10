package chim.logic.commands;

import static chim.commons.util.StringUtil.splitToKeywordsList;
import static chim.logic.commands.CommandTestUtil.VALID_CHEESE_TYPE_BRIE;
import static chim.logic.commands.CommandTestUtil.VALID_NAME_AMY;
import static chim.logic.commands.CommandTestUtil.VALID_ORDER_COMPLETE_STATUS;
import static chim.logic.commands.CommandTestUtil.VALID_PHONE_AMY;
import static chim.logic.commands.CommandTestUtil.assertCommandSuccess;
import static chim.model.Model.PREDICATE_SHOW_ALL_ORDERS;
import static chim.testutil.TypicalModels.getTypicalChim;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import chim.commons.core.Messages;
import chim.model.Model;
import chim.model.ModelManager;
import chim.model.UserPrefs;
import chim.model.customer.Customer;
import chim.model.order.Order;
import chim.model.order.predicates.OrderCheeseTypePredicate;
import chim.model.order.predicates.OrderCompletionStatusPredicate;
import chim.model.order.predicates.OrderNamePredicate;
import chim.model.order.predicates.OrderPhonePredicate;
import chim.model.util.predicate.CompositeFieldPredicateBuilder;
import chim.model.util.predicate.FieldPredicate;

/**
 * Contains integration tests (interaction with the Model) and unit tests for FindOrderCommand.
 */
public class FindOrderCommandTest {

    private Model model;
    private Model expectedModel;
    private List<Customer> customerList;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalChim(), new UserPrefs());
        expectedModel = new ModelManager(model.getChim(), new UserPrefs());
        expectedModel.setPanelToOrderList();
        customerList = model.getCompleteCustomerList();
    }

    @Test
    public void execute_listIsNotFiltered_showsEverything() {
        assertCommandSuccess(
                new FindOrderCommand(PREDICATE_SHOW_ALL_ORDERS),
                model,
                String.format(
                        Messages.MESSAGE_ORDERS_FOUND_OVERVIEW,
                        expectedModel.getFilteredOrderList().size(),
                        PREDICATE_SHOW_ALL_ORDERS),
                expectedModel
        );
    }

    @Test
    public void execute_listIsFilteredByCheeseTypeOnly() {
        List<String> cheeseTypeKeywords = splitToKeywordsList(VALID_CHEESE_TYPE_BRIE);
        OrderCheeseTypePredicate predicate = new OrderCheeseTypePredicate(cheeseTypeKeywords);
        expectedModel.updateFilteredOrderList(predicate);
        assertCommandSuccess(
                new FindOrderCommand(predicate),
                model,
                String.format(
                        Messages.MESSAGE_ORDERS_FOUND_OVERVIEW,
                        expectedModel.getFilteredOrderList().size(),
                        predicate),
                expectedModel
        );
    }

    @Test
    public void execute_listIsFiltered_byCustomerNameOnly() {
        List<String> nameKeywords = splitToKeywordsList(VALID_NAME_AMY);
        OrderNamePredicate predicate = new OrderNamePredicate(nameKeywords, customerList);
        expectedModel.updateFilteredOrderList(predicate);

        String expectedMessage = String.format(
                Messages.MESSAGE_ORDERS_FOUND_OVERVIEW,
                expectedModel.getFilteredOrderList().size(),
                predicate);

        if (expectedModel.getFilteredOrderList().size() == 0) {
            expectedMessage = String.format(
                    Messages.MESSAGE_ORDERS_NOT_FOUND_OVERVIEW,
                    predicate
            );
        }

        assertCommandSuccess(
                new FindOrderCommand(predicate),
                model,
                expectedMessage,
                expectedModel
        );
    }

    @Test
    public void execute_listIsFiltered_byCustomerPhoneOnly() {
        List<String> phoneKeywords = splitToKeywordsList(VALID_PHONE_AMY);
        OrderPhonePredicate predicate = new OrderPhonePredicate(phoneKeywords, customerList);
        expectedModel.updateFilteredOrderList(predicate);

        String expectedMessage = String.format(
                Messages.MESSAGE_ORDERS_FOUND_OVERVIEW,
                expectedModel.getFilteredOrderList().size(),
                predicate);

        if (expectedModel.getFilteredOrderList().size() == 0) {
            expectedMessage = String.format(
                    Messages.MESSAGE_ORDERS_NOT_FOUND_OVERVIEW,
                    predicate
            );
        }

        assertCommandSuccess(
                new FindOrderCommand(predicate),
                model,
                expectedMessage,
                expectedModel
        );
    }

    @Test
    public void execute_listIsFiltered_byCompletionStatusOnly() {
        OrderCompletionStatusPredicate predicate = new OrderCompletionStatusPredicate(VALID_ORDER_COMPLETE_STATUS);
        expectedModel.updateFilteredOrderList(predicate);

        String expectedMessage = String.format(
                Messages.MESSAGE_ORDERS_FOUND_OVERVIEW,
                expectedModel.getFilteredOrderList().size(),
                predicate);

        if (expectedModel.getFilteredOrderList().size() == 0) {
            expectedMessage = String.format(
                    Messages.MESSAGE_ORDERS_NOT_FOUND_OVERVIEW,
                    predicate
            );
        }

        assertCommandSuccess(
                new FindOrderCommand(predicate),
                model,
                expectedMessage,
                expectedModel
        );
    }

    @Test
    public void execute_listIsFiltered_byCheeseTypeAndCompletionStatus() {
        FieldPredicate<Order> predicate = new CompositeFieldPredicateBuilder<Order>()
                .compose(new OrderCheeseTypePredicate(splitToKeywordsList(VALID_CHEESE_TYPE_BRIE)))
                .compose(new OrderCompletionStatusPredicate(VALID_ORDER_COMPLETE_STATUS))
                .compose(new OrderNamePredicate(splitToKeywordsList(VALID_NAME_AMY), customerList))
                .compose(new OrderPhonePredicate(splitToKeywordsList(VALID_PHONE_AMY), customerList))
                .build();

        expectedModel.updateFilteredOrderList(predicate);

        String expectedMessage = String.format(
                Messages.MESSAGE_ORDERS_FOUND_OVERVIEW,
                expectedModel.getFilteredOrderList().size(),
                predicate);

        if (expectedModel.getFilteredOrderList().size() == 0) {
            expectedMessage = String.format(
                    Messages.MESSAGE_ORDERS_NOT_FOUND_OVERVIEW,
                    predicate
            );
        }

        assertCommandSuccess(
                new FindOrderCommand(predicate),
                model,
                expectedMessage,
                expectedModel
        );
    }
}