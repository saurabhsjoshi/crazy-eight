import React, {useEffect, useState} from "react";
import useWebSocket, {ReadyState} from "react-use-websocket";
import {Container, Row} from "react-bootstrap";
import PlayerScoresTable, {PlayerScore} from "./PlayerScoresTable";
import Button from "react-bootstrap/Button";
import PlayerHand, {Card, Rank, Suit} from "./PlayerHand";

export function GameScreen(props: { username: string }) {
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
      if (data["type"] === "PlayerList") {
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
      if (data["type"] === "Host") {
        setHost(true);
      }

      if(data["type"] === "StartRound") {
        const cards : Card[] = data["cards"]
            .map((c: any) => {
              return {
                suit: Suit[c.suit],
                rank: Rank[c.rank]
              };
            });
        setPlayerHand(cards);
        setGameStarted(true);
      }

    }
  }, [lastMessage]);

  const onStartGame = () => {
    sendJsonMessage({
      "type": "StartGame"
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
            gameStarted &&
            <PlayerHand myTurn={false} hand={playerHand}/>
        }
      </Container>
  )
}