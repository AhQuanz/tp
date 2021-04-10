package chim.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import chim.commons.core.Messages;
import chim.commons.core.index.Index;
import chim.logic.commands.exceptions.CommandException;
import chim.model.Model;
import chim.model.cheese.Cheese;

/**
 * Deletes a cheese identified using its displayed index from the cheese list.
 */
public class DeleteCheeseCommand extends DeleteCommand {

    public static final String COMMAND_WORD = "deletecheese";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the cheese identified by the index number used in the displayed cheese list.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_DELETE_CHEESE_SUCCESS = "Deleted Cheese: %1$s";

    private final Index targetIndex;

    public DeleteCheeseCommand(Index targetIndex) {
        this.targetIndex = targetIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Cheese> lastShownList = model.getFilteredCheeseList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_CHEESE_DISPLAYED_INDEX);
        }

        Cheese cheeseToDelete = lastShownList.get(targetIndex.getZeroBased());

        if (cheeseToDelete.isCheeseAssigned()) {
            throw new CommandException(Messages.MESSAGE_INVALID_CHEESE_ASSIGNED_TO_COMPLETED_ORDER);
        }

        model.deleteCheese(cheeseToDelete);
        model.setPanelToCheeseList(); // Display cheese list
        return new CommandResult(String.format(MESSAGE_DELETE_CHEESE_SUCCESS, cheeseToDelete));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof DeleteCheeseCommand // instanceof handles nulls
                && targetIndex.equals(((DeleteCheeseCommand) other).targetIndex)); // state check
    }
}