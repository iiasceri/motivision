package com.inther.controller;

import com.inther.EntityNotFoundException;
import com.inther.entity.Activities;
import com.inther.entity.Character;
import com.inther.entity.Items;
import com.inther.entity.Team;
import com.inther.repo.ActivitiesRepository;
import com.inther.repo.CharacterRepository;
import com.inther.repo.TeamRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class TeamController {

    private Logger log = Logger.getLogger(TeamController.class);
    @Autowired
    TeamRepository teamRepository;

    @Autowired
    ActivitiesRepository activitiesRepository;

    @Autowired
    CharacterRepository characterRepository;

    /*
     * Get team info request
     * Used to get team info by teamId
     * @param teamId - id of the team to get data
     * @return if such team exist return Json info about the team
     * @return if no such team exist return Json fail message
     * */
    @RequestMapping(value = "/get_team", method = RequestMethod.GET)
    public Map<String, Object> getTeam(@RequestParam(value = "teamId") Long teamId) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        Optional<Team> optionalTeam = teamRepository.findTeamById(teamId);
        if (!optionalTeam.isPresent()) {
            log.warn("Invalid teamId, Team not found");
            map.put("status", "failed");
            map.put("message", "team not found");
            return map;
        }
        log.info("Team with teamId found");
        Team team = optionalTeam.get();
        map.put("status", "success");
        map.put("teamId", team.getId());
        map.put("teamName", team.getName());
        map.put("liderId", team.getAdmin().getId());
        map.put("teamLogo", team.getTeamLogo());
        map.put("battleFrequency", team.getBattleFrequency());
        map.put("teamWins", team.getTeamWins());
        map.put("teamLoss", team.getTeamLoss());
        map.put("lock", team.getLock());
        log.info("Team characters received");
        List<Character> characterList = characterRepository.findAllByTeamId(teamId);
        List<Map<String, Object>> result = new ArrayList<>();
        log.info("Parsing team characters");
        for (Character character : characterList) {
            Map<String, Object> characterMap = new LinkedHashMap<>();
            characterMap.put("characterId", character.getId());
            characterMap.put("characterName", character.getName());
            characterMap.put("playerId", character.getPlayer().getId());
            characterMap.put("teamId", character.getTeam().getId());
            characterMap.put("isAdmin", String.valueOf(character.getTeam().getAdmin().getId().equals(character.getId())));
            characterMap.put("headType", character.getHeadType());
            characterMap.put("bodyType", character.getBodyType());
            characterMap.put("gender", character.getGender());
            characterMap.put("points", character.getPoints());
            result.add(characterMap);
        }
        map.put("characters", result);
        log.info("Team data successfully returned");
        return map;
    }

    /*
     * Get team members request
     * Used to get a list of all team members
     * @param teamId - team to search characters in
     * @return status - failed if no such team exist
     * @return status - success if request was successful
     * @return teamMembers - null if no teamMembers
     * @return teamMembers - list of all teamMembers
     * */
    @RequestMapping(value = "/get_team_members", method = RequestMethod.GET)
    public Map<String, Object> getTeamMembers(@RequestParam(value = "teamId") Long teamId) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        Optional<Team> optionalTeam = teamRepository.findById(teamId);
        if (!optionalTeam.isPresent()) {
            log.warn("Team with teamId " + teamId + " not found");
            map.put("status", "failed");
            map.put("message", "no such team with teamId exist");
            return map;
        }
        log.info("Team found");
        Optional<List<Character>> optionalCharacterList = characterRepository.findByTeamId(teamId);
        if (!optionalCharacterList.isPresent()) {
            log.info("Team doesn't have any characters");
            map.put("status", "success");
            map.put("teamMembers", "null");
            return map;
        }
        log.info("Team has some characters");
        List<Character> characterList = optionalCharacterList.get();
        map.put("status", "success");
        Iterator<Character> iterator = characterList.iterator();
        ArrayList<Map<String, Object>> charactersArrayList = new ArrayList<>();
        while (iterator.hasNext()) {
            LinkedHashMap<String, Object> characterMap = new LinkedHashMap<>();
            Character character = iterator.next();
            characterMap.put("characterId", String.valueOf(character.getId()));
            characterMap.put("characterName", character.getName());
            characterMap.put("headType", character.getHeadType());
            characterMap.put("bodyType", character.getBodyType());
            characterMap.put("gender", character.getGender());
            characterMap.put("points", character.getPoints());
            charactersArrayList.add(characterMap);
        }
        log.info("Team members returned successfully");
        map.put("teamMembers", charactersArrayList);
        return map;
    }

    /*
     *  Create team request
     *  Used to create new team and add default 6 activities to it
     *  @param name - team name
     *  @param logo - team logo
     *  @param battleFrequency - team battleFrequency
     *  @return status failed if team name already exists
     *  @return status failed if somehow default activities are not present
     *  @return status success if team was successfully created
     * */
    @RequestMapping(value = "/create_team", method = RequestMethod.POST)
    public Map<String, Object> createTeam(@RequestParam(value = "teamName") String name,
                                          @RequestParam(value = "teamLogo") String logo,
                                          @RequestParam(value = "battleFrequency") Integer battleFrequency) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        Optional<Team> optionalTeam = teamRepository.findByName(name);
        if (optionalTeam.isPresent()) {
            log.warn("Invalid team name, Team already exist");
            map.put("status", "failed");
            map.put("message", "team name already exist");
            return map;
        }
        log.info("Valid team name, Team can be created");
        Team team = new Team();
        team.setName(name);
        team.setTeamLogo(logo);
        team.setBattleFrequency(battleFrequency);
        team.setLock(false);
        log.info("Team creation completed");
        teamRepository.save(team);
        log.info("Team saved to the database");
        map.put("status", "success");
        map.put("teamId", team.getId());
        return map;
    }

    /*
     * Delete team request
     * Used to delete a team by teamId
     * @param teamId - team for deletion
     * @return status failed if no such team exists
     * @return status success if team was deleted successfully
     * */
    @RequestMapping(value = "/delete_team", method = RequestMethod.DELETE)
    public Map<String, Object> deleteTeam(@RequestParam(value = "teamId") Long teamId) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        Optional<Team> optionalTeam = teamRepository.findById(teamId);
        if (!optionalTeam.isPresent()) {
            log.warn("Ivalid teamId, no such Team found in database");
            map.put("status", "failed");
            map.put("message", "no such team exist");
            return map;
        }
        log.info("Valid teamId, Team for deletion found");
        log.info("Team up for deletion");
        teamRepository.deleteById(teamId);
        log.info("Team successfully deleted");
        map.put("status", "success");
        return map;
    }

    /*
     * Update team request
     * Used to update a team by teamId
     * @param teamId - team for updating
     * @param teamLogo - new team logo
     * @param battleFrequency - new battle frequency
     * @param lock - lock or unlock a team
     * @return status failed if no such team exists
     * @return status success if team was deleted successfully
     * */
    @RequestMapping(value = "/update_team", method = RequestMethod.POST)
    public Map<String, Object> updateTeam(@RequestParam(value = "teamId") Long teamId,
                                          @RequestParam(value = "teamLogo") String teamLogo,
                                          @RequestParam(value = "battleFrequency") Integer frequency,
                                          @RequestParam(value = "lock") boolean lock) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        Optional<Team> optionalTeam = teamRepository.findById(teamId);
        if (!optionalTeam.isPresent()) {
            log.warn("Ivalid teamId, no such Team found in database");
            map.put("status", "failed");
            map.put("message", "no such team exist");
            return map;
        }
        log.info("Team found");
        Team team = optionalTeam.get();
        team.setTeamLogo(teamLogo);
        team.setBattleFrequency(frequency);
        team.setLock(lock);
        teamRepository.save(team);
        log.info("Team updated successfully");
        map.put("status", "success");
        return map;
    }

    /*
     * Team exist request
     * Used to check if a team exists by teamName
     * @param teamName - team name for deletion
     * @return status failed if no such team exists
     * @return status succes with teamId and with lock status if team is locked
     * */
    @RequestMapping(value = "/team_exist", method = RequestMethod.POST)
    public Map<String, String> teamExist(@RequestParam(name = "teamName") String name) {
        TreeMap<String, String> map = new TreeMap<>();
        Optional<Team> teamOptional = teamRepository.findByName(name);
        if (!teamOptional.isPresent()) {
            log.info("Team not found");
            map.put("status", "success");
            map.put("message", "false");
            return map;
        }
        log.info("Team found");
        map.put("status", "success");
        Team team = teamOptional.get();
        map.put("teamId", team.getId().toString());
        map.put("lock", team.getLock().toString());
        return map;
    }
}
