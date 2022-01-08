package com.crud.tasks.trello.client;

import com.crud.tasks.domain.CreatedTrelloCard;
import com.crud.tasks.domain.TrelloBoardDto;
import com.crud.tasks.domain.TrelloCardDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;

@Component
@RequiredArgsConstructor
public class TrelloClient {
    private final RestTemplate restTemplate;

    @Value("${trello.api.endpoint.prod}")
    private String trelloApiEndpoint;
    @Value("${trello.app.key}")
    private String trelloAppKey;
    @Value("${trello.app.token}")
    private String trelloToken;
    @Value("${trello.app.username}")
    private String trelloUsername;

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

        URI url = buildURIAddress(trelloApiEndpoint, trelloUsername, trelloAppKey, trelloToken, "name,id", "all");
        TrelloBoardDto[] boardsResponse = restTemplate.getForObject( url, TrelloBoardDto[].class );

        return Optional.ofNullable(boardsResponse)
                .map(Arrays::asList)
                .orElse(Collections.emptyList());
    }

    public CreatedTrelloCard createNewCard(TrelloCardDto trelloCardDto) {
        URI url = UriComponentsBuilder.fromHttpUrl(trelloApiEndpoint + "/cards")
                .queryParam("key", trelloAppKey)
                .queryParam("token", trelloToken)
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
