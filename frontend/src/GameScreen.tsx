import React, {useEffect, useState} from "react";
import useWebSocket, {ReadyState} from "react-use-websocket";
import {Container, Row} from "react-bootstrap";
import PlayerScoresTable, {PlayerScore} from "./PlayerScoresTable";
import Button from "react-bootstrap/Button";
import PlayerHand, {Card, onCardClick, onDrawCardClick, Rank, Suit, Turn} from "./PlayerHand";

export function GameScreen(props: { username: string }) {
  const [turnInfo, setTurnInfo] = useState<Turn | null>(null);
  const [gameStarted, setGameStarted] = useState<boolean>(false);
  const [isHost, setHost] = useState<boolean>(false);
  const [socketUrl] = useState('ws://localhost:8080/api');
  const {sendJsonMessage, lastMessage, readyState} = useWebSocket(socketUrl);
  const [playerScores, setPlayerScores] = useState<PlayerScore[]>([]);
  const [playerHand, setPlayerHand] = useState<Card[]>([]);

  const connectionStatus = {
    [ReadyState.CONNECTING]: 'Connecting',
    [ReadyState.OPEN]: 'Connected',
    [ReadyState.CLOSING]: 'Closing',
    [ReadyState.CLOSED]: 'Closed',
    [ReadyState.UNINSTANTIATED]: 'Uninstantiated',
  }[readyState];

  enum RegistrationStatus {
    NOT_REGISTERED,
    REGISTERED
  }

  const [registerState, setRegisterState] = useState(RegistrationStatus.NOT_REGISTERED);

  const registrationStatus = {
    [RegistrationStatus.REGISTERED]: 'Registered',
    [RegistrationStatus.NOT_REGISTERED]: 'Not Registered'
  }[registerState];

  useEffect(() => {
    if (readyState === ReadyState.OPEN && registerState === RegistrationStatus.NOT_REGISTERED) {
      sendJsonMessage({
        "type": "UserRegister",
        "username": props.username
      });
      setRegisterState(RegistrationStatus.REGISTERED);
    }
  }, [readyState, registerState, RegistrationStatus.NOT_REGISTERED,
    RegistrationStatus.REGISTERED, sendJsonMessage, props.username]);

  useEffect(() => {
    if (lastMessage !== null) {
      const data = JSON.parse(lastMessage.data);
      const type = data["type"];

      if (type === "PlayerList") {
        const p: PlayerScore[] = data["players"].map((item: any) => {
          return {
            username: item.username,
            score: item.score
          };
        });
        setPlayerScores(p);
        return;
      }

      // We are the host
      if (type === "Host") {
        setHost(true);
        return;
      }

      if (type === "StartRound") {
        const cards: Card[] = data["cards"]
            .map((c: any) => {
              return {
                suit: Suit[c.suit as keyof typeof Suit],
                rank: Rank[c.rank as keyof typeof Rank]
              };
            });
        setPlayerHand(cards);
        setGameStarted(true);
        return;
      }

      if (type === "StartTurn") {
        let card: Card = {
          suit: Suit[data["topCard"].suit as keyof typeof Suit],
          rank: Rank[data["topCard"].rank as keyof typeof Rank]
        }

        setTurnInfo({
          topCard: card,
          cardsToDraw: data["cardsToDraw"] as number,
          username: data["username"] as string,
          error: undefined,
          cardsDrawn: 0
        });
        return;
      }

      if (type === "UpdateHand") {
        const cards: Card[] = data["cards"]
            .map((c: any) => {
              return {
                suit: Suit[c.suit as keyof typeof Suit],
                rank: Rank[c.rank as keyof typeof Rank]
              };
            });
        setPlayerHand(cards);
        return;
      }
    }
  }, [lastMessage]);

  const onStartGame = () => {
    sendJsonMessage({
      "type": "StartGame"
    });
  }

  const onCardClicked: onCardClick = (e) => {
    setTurnInfo(currentState => {
      if (!currentState) {
        return currentState;
      }

      const btn = e.target as HTMLInputElement;

      if(btn.id === "passBtn") {
        // Player has passed this round
        sendJsonMessage({
          "type": "CompleteTurn"
        });
        return {...currentState, username: ""};
      }

      let cardPlayed = playerHand[Number(btn.getAttribute("data-idx"))];

      if (cardPlayed.rank === currentState.topCard.rank || cardPlayed.suit === currentState.topCard.suit) {
        console.log("Valid card " + btn.textContent);
        sendJsonMessage({
          "type": "CompleteTurn",
          "card": btn.textContent
        });
        return {...currentState, username: ""};
      }

      console.log("Invalid card");
      return {...currentState, error: "Invalid card"};
    });
  };

  const onDrawClicked: onDrawCardClick = () => {
    setTurnInfo(currentState => {
      if (!currentState) {
        return currentState;
      }
      return {...currentState, cardsDrawn: currentState.cardsDrawn + 1};
    });

    sendJsonMessage({
      "type": "DrawCard",
    });
  }

  return (
      <Container className="max-width">
        <Row>
          <h1>Crazy Eights</h1>
        </Row>
        <Row>
          <h6 id="usernameLbl">Username: {props.username}</h6>
        </Row>
        <Row>
          <h6 id="connectionLbl">Connection Status: {connectionStatus}</h6>
        </Row>
        <Row>
          <h6 id="userRegisterLbl">User Registration: {registrationStatus}</h6>
        </Row>
        <Row>
          {turnInfo !== null && <h6 id="currentTurn">Current Turn: {turnInfo?.username}</h6>}
        </Row>
        <PlayerScoresTable playerScores={playerScores}/>
        <Container>
          {
              isHost &&
              !gameStarted &&
              <Button onClick={onStartGame}
                      disabled={playerScores.length < 2} id="startGameBtn"
                      className="btn btn-primary">Start Game</Button>
          }
        </Container>

        {
            gameStarted && turnInfo !== null &&
            <PlayerHand drawCardClicked={onDrawClicked} cardClicked={onCardClicked} username={props.username}
                        hand={playerHand} turnInfo={turnInfo}/>
        }
      </Container>
  )
}