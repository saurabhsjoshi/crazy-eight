import {Container, Row} from "react-bootstrap";
import Button from "react-bootstrap/Button";
import React from "react";

export interface Turn {
  topCard: Card
  cardsToDraw: number
  username: string
  error: string | undefined

  cardsDrawn: number
}

export type onCardClick = (e: React.MouseEvent<HTMLElement>) => void;
export type onDrawCardClick = (e: React.MouseEvent<HTMLElement>) => void;

export enum Suit {
  CLUBS,
  DIAMONDS,
  HEART,
  SPADES,
}

export enum Rank {
  ACE,
  TWO,
  THREE,
  FOUR,
  FIVE,
  SIX,
  SEVEN,
  EIGHT,
  NINE,
  TEN,
  JACK,
  QUEEN,
  KING
}

export interface Card {
  suit: Suit;
  rank: Rank;
}

function toText(c: Card | undefined): string {

  let s: string = "";
  let r: string = "";

  if (c == null) {
    return r + s;
  }

  switch (c.rank) {
    case Rank.ACE: {
      r = "1";
      break;
    }
    case Rank.TWO: {
      r = "2";
      break;
    }
    case Rank.THREE: {
      r = "3";
      break;
    }
    case Rank.FOUR: {
      r = "4";
      break;
    }
    case Rank.FIVE: {
      r = "5";
      break;
    }
    case Rank.SIX: {
      r = "6";
      break;
    }
    case Rank.SEVEN: {
      r = "7";
      break;
    }
    case Rank.EIGHT: {
      r = "8";
      break;
    }
    case Rank.NINE: {
      r = "9";
      break;
    }
    case Rank.TEN: {
      r = "10";
      break;
    }
    case Rank.JACK: {
      r = "J";
      break;
    }
    case Rank.QUEEN: {
      r = "Q";
      break;
    }
    case Rank.KING: {
      r = "K";
      break;
    }
  }

  switch (c.suit) {
    case Suit.CLUBS:
      s = "C";
      break;
    case Suit.DIAMONDS:
      s = "D";
      break;
    case Suit.HEART:
      s = "H";
      break;
    case Suit.SPADES:
      s = "S";
      break;
  }

  return r + s;
}

function PlayerHand(props: {
  username: string, hand: Card[],
  turnInfo: Turn | null,
  cardClicked: onCardClick,
  drawCardClicked: onDrawCardClick
}) {
  return (
      <Container className="mt-4">
        <Row>
          <h5>Your Hand</h5>
        </Row>
        <div id="playerHandBtnGrp" className="btn-group btn-group-lg" role="group">
          {
            props.hand.map(c => toText(c))
                .map((c, index) =>
                    <Button onClick={props.cardClicked}
                            key={c}
                            data-idx={index}
                            disabled={props.username !== props.turnInfo?.username}
                            className="btn">
                      {c}
                    </Button>
                )
          }
        </div>
        <Row className="mt-4">
          <div id="playerHandBtnGrp" className="btn-group btn-group-lg" role="group">
            <Button
                id="drawCardBtn"
                onClick={props.drawCardClicked}
                className="btn btn-info"
                disabled={(props.username !== props.turnInfo?.username) || props.turnInfo.cardsDrawn > 2}>
              Draw Card
            </Button>
            <Button
                id="passBtn"
                onClick={props.cardClicked}
                className="btn btn-danger"
                hidden={(props.username !== props.turnInfo?.username) || props.turnInfo.cardsDrawn < 3}>
              End Round
            </Button>
          </div>
        </Row>

        <Row className="align-items-center align-content-center mt-3">
          <h5>Top Card {toText(props.turnInfo?.topCard)}</h5>
        </Row>
      </Container>
  );
}

export default PlayerHand;