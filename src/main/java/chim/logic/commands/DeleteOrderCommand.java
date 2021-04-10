package chim.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import chim.commons.core.Messages;
import chim.commons.core.index.Index;
import chim.logic.commands.exceptions.CommandException;
import chim.model.Model;
import chim.model.customer.Name;
import chim.model.order.Order;

/**
 * Deletes an order identified using its displayed index from the order list.
 */
public class DeleteOrderCommand extends DeleteCommand {

    public static final String COMMAND_WORD = "deleteorder";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the order identified by the index number used in the displayed order list.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_DELETE_ORDER_SUCCESS = "Deleted Order: %1$s ; Customer Name: %2$s";

    private final Index targetIndex;

    public DeleteOrderCommand(Index targetIndex) {
        this.targetIndex = targetIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        Order orderToDelete = getOrderToDelete(model);
        model.deleteOrder(orderToDelete);
        model.setPanelToOrderList(); // Display order list
        Name name = model.getCustomerWithId(orderToDelete.getCustomerId()).getName();
        return new CommandResult(String.format(MESSAGE_DELETE_ORDER_SUCCESS, orderToDelete, name));
    }

    /**
     * Obtains the {@code Order} to delete using the target index.
     */
    private Order getOrderToDelete(Model model) throws CommandException {
        List<Order> lastShownList = model.getFilteredOrderList();
        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_ORDER_DISPLAYED_INDEX);
        }
        return lastShownList.get(targetIndex.getZeroBased());
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof DeleteOrderCommand // instanceof handles nulls
                && targetIndex.equals(((DeleteOrderCommand) other).targetIndex)); // state check
    }
}