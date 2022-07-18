package com.fthlbot.discordbotfthl.Events

import com.fthlbot.discordbotfthl.DatabaseModels.Division.Division
import com.fthlbot.discordbotfthl.DatabaseModels.Team.TeamService
import com.fthlbot.discordbotfthl.Util.BotConfig
import com.fthlbot.discordbotfthl.core.Annotation.AllowedChannel
import com.fthlbot.discordbotfthl.core.Annotation.CommandType
import com.fthlbot.discordbotfthl.core.Annotation.Invoker
import com.fthlbot.discordbotfthl.core.Handlers.Command
import org.apache.commons.lang3.tuple.Pair
import org.javacord.api.entity.permission.Role
import org.javacord.api.entity.server.Server
import org.javacord.api.entity.user.User
import org.javacord.api.event.interaction.SlashCommandCreateEvent

@Invoker(
    alias = "give-role-to-rep",
    description = "Gives reps their roles on the main server",
    usage = "/give-role-to-rep",
    type = CommandType.STAFF,
    where = AllowedChannel.MAIN_SERVER

)
class GiveRolesImpl(
    private val teamService: TeamService,
    private val config: BotConfig
    ) : Command {

    override fun execute(event: SlashCommandCreateEvent) {
        val allTeams = teamService.allTeams

        //Guaranteed to be present.

        val server = event.api.getServerById(config.fthlServerID).get()
        for (team in allTeams) {
            val rep1 : User? = event.api.getUserById(team.rep1ID).join()
            val rep2 : User? = event.api.getUserById(team.rep2ID).join()

            if (rep1 == null || rep2 == null) continue;

            if (!doTheyHaveRole(team.division, rep1.getRoles(server))){
                val findRoles = findRoles(server, team.division.name)
                server.addRoleToUser(rep1, findRoles.left)
                server.addRoleToUser(rep2, findRoles.right)
            }
        }
    }

    private fun doTheyHaveRole(division: Division, roles: MutableList<Role>) : Boolean {
        val map = roles.stream().map { it.name }
        return map.anyMatch { it.equals(division.alias) } || map.anyMatch { it.equals("Representative")}
    }

    private fun findRoles(server: Server, divName: String) :  Pair<Role, Role> {
        val filter = server.roles.stream().filter {
            it.name.equals(divName)
        }
        val findFirst = filter.findFirst().get()

        val filter2 = server.roles.stream().filter {
            it.name.equals("Representative")
        }
        val findFirst2 = filter2.findFirst().get()

        return Pair.of(findFirst, findFirst2)
    }
}