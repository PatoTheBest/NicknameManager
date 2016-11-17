package be.spyproof.nickmanager.commands.moderator;


import be.spyproof.nickmanager.commands.AbstractCmd;
import be.spyproof.nickmanager.commands.argument.PlayerDataArg;
import be.spyproof.nickmanager.commands.checks.IArgumentChecker;
import be.spyproof.nickmanager.controller.ISpongePlayerController;
import be.spyproof.nickmanager.controller.MessageController;
import be.spyproof.nickmanager.model.PlayerData;
import be.spyproof.nickmanager.util.Reference;
import be.spyproof.nickmanager.util.TemplateUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Map;
import java.util.Optional;

/**
 * Created by Spyproof.
 */
public class GiveNickChangesCmd extends AbstractCmd implements IArgumentChecker
{
    private static final String[] ARGS = new String[]{"player", "amount"};

    private GiveNickChangesCmd(MessageController messageController, ISpongePlayerController playerController)
    {
        super(messageController, playerController);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException
    {
        PlayerData playerData = getArgument(args, ARGS[0]);
        Integer amount = getArgument(args, ARGS[1]);

        playerData.setTokensRemaining(amount + playerData.getTokensRemaining());
        this.getPlayerController().savePlayer(playerData);

        Optional<Player> player = Sponge.getServer().getPlayer(playerData.getUuid());
        if (player.isPresent())
            player.get().sendMessage(
                    this.getMessageController().getMessage(Reference.SuccessMessages.ADMIN_NICK_GIVE_RECEIVED)
                        .apply(TemplateUtils.getParameters("tokens", amount)).build());

        Map<String, Text> params = TemplateUtils.getParameters(playerData);
        params.putAll(TemplateUtils.getParameters("tokens", Text.of(amount)));

        src.sendMessage(this.getMessageController().getMessage(Reference.SuccessMessages.ADMIN_NICK_GIVE).apply(params).build());

        return CommandResult.success();
    }

    public static CommandSpec getCommandSpec(MessageController messageController, ISpongePlayerController playerController)
    {
        return CommandSpec.builder()
                          .arguments(new PlayerDataArg(ARGS[0], playerController),
                                     GenericArguments.integer(Text.of(ARGS[1])))
                          .executor(new GiveNickChangesCmd(messageController, playerController))
                          .permission(Reference.Permissions.ADMIN_GIVE)
                          .build();
    }
}
