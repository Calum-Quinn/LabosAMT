package ch.heigvd.amt.web;

import ch.heigvd.amt.beans.UserDTO;
import ch.heigvd.amt.repository.UserRepository;
import ch.heigvd.amt.service.UserMapper;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Leaderboard of the users with the most credits
 */
@Path("/leaderboard")
public class LeaderboardResource {

    @Inject
    UserRepository repository;

    @Inject
    UserMapper userMapper;

    @CheckedTemplate(requireTypeSafeExpressions = false)
    public static class Templates {
        public static native TemplateInstance leaderboard(List<UserDTO> users);
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance getLeaderboard() {

        // Fetch sorted list of users with bet counts
        List<UserDTO> userDTOS = repository.listLeaderboardWithBetCounts().stream()
                .map(userMapper::map)
                .toList();

        // Assign ranks based on position
        AtomicInteger rank = new AtomicInteger(1);
        userDTOS.forEach(user -> user.setRank(rank.getAndIncrement()));

        return LeaderboardResource.Templates.leaderboard(userDTOS);


        // BELOW ARE ATTEMPTS TO SHOW PROFILE PICTURE ON LEADERBOARD


//        List<UserDTO> userDTOS = repository.listLeaderboardWithBetCounts().stream()
//                .map(userMapper::map)
//                .toList();
//
//        // Assign ranks based on position
//        AtomicInteger rank = new AtomicInteger(1);
//        userDTOS.forEach(user -> user.setRank(rank.getAndIncrement()));
//
//        GatewayDiscordClient gateway = gatewayProvider.getGateway();
//
//        List<Mono<Void>> avatarUpdates = new ArrayList<>();
//
//        userDTOS.forEach(user -> {
//            Mono<Void> avatarUpdate = gateway.getUserById(Snowflake.of(String.valueOf(user.getId())))
//                    .flatMap(discordUser -> {
//                        // Set the profile picture URL directly
//                        user.setProfilePictureURL(discordUser.getAvatarUrl());
//                        return Mono.empty(); // Return an empty Mono<Void> after setting the URL
//                    });
//
//            avatarUpdates.add(avatarUpdate); // Collect the Mono for the avatar update
//        });

        // Combine all avatar updates and wait for them to complete
//        Mono<Void> allUpdates = Mono.when(avatarUpdates);
//        allUpdates.subscribe();

//        return LeaderboardResource.Templates.leaderboard(userDTOS);
    }
}
