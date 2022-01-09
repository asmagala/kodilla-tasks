package com.crud.tasks.trello.client;

import com.crud.tasks.domain.CreatedTrelloCard;
import com.crud.tasks.domain.TrelloBoardDto;
import com.crud.tasks.domain.TrelloCardDto;
import com.crud.tasks.trello.config.TrelloConfig;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TrelloClient {
    private final RestTemplate restTemplate;
    private static final Logger LOGGER = LoggerFactory.getLogger(TrelloClient.class);

    private final TrelloConfig trelloConfig;

    private URI buildURIAddress(final String apiEndpoint, final String username, final String key, final String token, final String fields, final String lists) {
        URI url = UriComponentsBuilder.fromHttpUrl(apiEndpoint + "/members/" + username + "/boards")
                .queryParam("key", key)
                .queryParam("token", token)
                .queryParam("fields", fields)
                .queryParam("lists", lists)
                .build()
                .encode()
                .toUri();
        return url;
    }

    public List<TrelloBoardDto> getTrelloBoards() {

        URI url = buildURIAddress(trelloConfig.getTrelloApiEndpoint(), trelloConfig.getTrelloUser(), trelloConfig.getTrelloAppKey(), trelloConfig.getTrelloToken(), "name,id", "all");

        try {
            TrelloBoardDto[] boardsResponse = restTemplate.getForObject( url, TrelloBoardDto[].class );
            return Optional.ofNullable(boardsResponse)
                    .map(Arrays::asList)
                    .orElse(Collections.emptyList())
                    .stream()
                    .filter(p -> Objects.nonNull(p.getId()) && Objects.nonNull(p.getName()))
                    .collect(Collectors.toList());
        } catch (RestClientException e){
            LOGGER.error(e.getMessage(), e);
            return Collections.emptyList();
        }

    }

    public CreatedTrelloCard createNewCard(TrelloCardDto trelloCardDto) {
        URI url = UriComponentsBuilder.fromHttpUrl(trelloConfig.getTrelloApiEndpoint() + "/cards")
                .queryParam("key", trelloConfig.getTrelloAppKey())
                .queryParam("token", trelloConfig.getTrelloToken())
                .queryParam("name", trelloCardDto.getName())
                .queryParam("desc", trelloCardDto.getDescription())
                .queryParam("pos", trelloCardDto.getPos())
                .queryParam("idList", trelloCardDto.getListId())
                .build()
                .encode()
                .toUri();
        return restTemplate.postForObject(url, null, CreatedTrelloCard.class);
    }
}
