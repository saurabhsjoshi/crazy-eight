import {Container, Row, Table} from "react-bootstrap";
import React from "react";

export interface PlayerScore {
  username: string;
  score: number;
}

function PlayerScoresTable(props: { playerScores: PlayerScore[] }) {
  return (
      <Container>
        <Row>
          <h4>Players</h4>
        </Row>
        <Table id="playerScoresTable" className="table table-striped">
          <tbody>
          <tr id="playerNameRow">
            <th scope="row">User</th>
            {
              props.playerScores.map(p => {
                return <td key={p.username}>{p.username}</td>
              })
            }
          </tr>
          <tr id="playerScoresRow">
            <th scope="row">Score</th>
            {
              props.playerScores.map(p => {
                return <td id={"scoreLbl_" + p.username} key={p.username}>{p.score}</td>
              })
            }
          </tr>
          </tbody>
        </Table>
      </Container>
  );
}

export default PlayerScoresTable;