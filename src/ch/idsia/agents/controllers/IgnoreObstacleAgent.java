/*
 * Copyright (c) 2009-2010, Sergey Karakovskiy and Julian Togelius
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Mario AI nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package ch.idsia.agents.controllers;

import ch.idsia.agents.Agent;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment;
import ch.idsia.benchmark.mario.engine.sprites.Sprite;
import ch.idsia.benchmark.mario.engine.GeneralizerLevelScene;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy, sergey.karakovskiy@gmail.com
 * Date: Apr 8, 2009
 * Time: 4:03:46 AM
 */

public class IgnoreObstacleAgent extends BasicMarioAIAgent implements Agent
{
int trueJumpCounter = 0;
int trueSpeedCounter = 0;
int trueLeftCounter = 0;
int rightCounter = 0;
int jumpCounter = 0;
boolean isJumpingOverGap = false;
int trappedCounter = 0;
boolean isTrapped = false;
int flowerCounter = 0;
int landAboveCounter = 0;
boolean isClimbingLand = false;
boolean isTakeOff = false;
int waitCounter = 0;

public IgnoreObstacleAgent()
{
    super("IgnoreObstacleAgent");
    reset();
}

public void reset()
{
    action = new boolean[Environment.numberOfKeys];
    action[Mario.KEY_RIGHT] = true;
	trueLeftCounter = 0;
    jumpCounter = 0;
    isJumpingOverGap = false;
    trappedCounter = 0;
    isTrapped = false;
    flowerCounter = 0;
    landAboveCounter = 0;
    isClimbingLand = false;
    isTakeOff = false;
    waitCounter = 0;

}

public boolean isObstacle(int r, int c){
	return getReceptiveFieldCellValue(r, c)==GeneralizerLevelScene.BRICK
			|| getReceptiveFieldCellValue(r, c)==GeneralizerLevelScene.BORDER_CANNOT_PASS_THROUGH
			|| getReceptiveFieldCellValue(r, c)==GeneralizerLevelScene.FLOWER_POT_OR_CANNON
			|| getReceptiveFieldCellValue(r, c)==GeneralizerLevelScene.LADDER;
}

private boolean isGap(int r, int c) {
	for (int i = 1; i < 18; i++) {
        if (isObstacle(r - 9 + i, c)) {
            return false;
        }
	}
    return true;
}

private boolean isWideGap(int r, int c) {
	for (int i = 1; i < 18; i++) {
        for (int j = 1; j < 8; j++) {
            if (isObstacle(r - 9 + i, c + j)) {
                return false;
            }
        }
	}
    return true;
}

private boolean isLandAbove(int r, int c) {
	for (int i = 1; i < 4; i++) {
		if (getReceptiveFieldCellValue(r - 2 + i, c) == GeneralizerLevelScene.BORDER_HILL ) {
			return true;
		}
	}
    return false;
}


private boolean isLandBelow() {
	for (int i = 0; i < 4; i++) {
		if (getReceptiveFieldCellValue(marioEgoRow + i, marioEgoCol) == GeneralizerLevelScene.BORDER_HILL ) {
			return true;
		}
	}
    return false;
}

public boolean isEnemyBelow(int r, int c) {
	for (int i = 1; i < 3; i++) {
		if (getEnemiesCellValue(r + i, c) != Sprite.KIND_NONE) {
			return true;
		}
	}
    return false;
}

public boolean getActionForTube(int r, int c) {
    for (int i = 1; i < 8; i++) {
        if (getEnemiesCellValue(r + 4 - i, c) == Sprite.KIND_ENEMY_FLOWER) {
            return true;
        }
    }
    return false;
}

public boolean[] getAction()
{
    action[Mario.KEY_SPEED] = false;
    action[Mario.KEY_UP] = false;

    // if (!isObstacle(marioEgoRow + 1, marioEgoCol + 1)) {
    //     action[Mario.KEY_JUMP] = isMarioAbleToJump || !isMarioOnGround;
    // }
    if (isGap(marioEgoRow, marioEgoCol + 1) || isGap(marioEgoRow, marioEgoCol + 2)) {
        if (isMarioOnGround) {
            if (jumpCounter >= 2) {
                action[Mario.KEY_SPEED] = true;
                action[Mario.KEY_RIGHT] = true;
                action[Mario.KEY_JUMP] = isMarioAbleToJump || !isMarioOnGround;
                isJumpingOverGap = true;
                jumpCounter = 0;
            } else {
                action[Mario.KEY_RIGHT ] = false;
                action[Mario.KEY_JUMP] = false;
                jumpCounter++;
            }
        } else if (!isJumpingOverGap) {
            action[Mario.KEY_RIGHT] = false;
            action[Mario.KEY_LEFT] = true;
            action[Mario.KEY_JUMP] = false;
            action[Mario.KEY_SPEED] = true;
            jumpCounter++;
        }
    } else {
            jumpCounter = 0;
            isJumpingOverGap = false;
            action[Mario.KEY_LEFT] = false;
            action[Mario.KEY_RIGHT] = true;
    }

    if (landAboveCounter <= 12 && (
        isLandAbove(marioEgoRow, marioEgoCol - 1) ||
        isLandAbove(marioEgoRow, marioEgoCol) ||
        isLandAbove(marioEgoRow, marioEgoCol + 1)) ) {
        if (isLandAbove(marioEgoRow, marioEgoCol - 1)) {
            action[Mario.KEY_RIGHT] = false;
            action[Mario.KEY_LEFT] = true;
            action[Mario.KEY_SPEED] = false;
            action[Mario.KEY_JUMP] = isMarioAbleToJump || !isMarioOnGround;
            landAboveCounter ++;
        } else if (isLandAbove(marioEgoRow, marioEgoCol + 1)) {
            action[Mario.KEY_RIGHT] = true;
            action[Mario.KEY_LEFT] = false;
            action[Mario.KEY_JUMP] = false;
            landAboveCounter ++;
        } else if (isLandAbove(marioEgoRow, marioEgoCol)){
            action[Mario.KEY_RIGHT] = false;
            action[Mario.KEY_LEFT] = true;
            action[Mario.KEY_SPEED] = false;
            action[Mario.KEY_JUMP] = isMarioAbleToJump || !isMarioOnGround;
            landAboveCounter ++;
        }
    } else if (landAboveCounter > 25) {
        isTakeOff = false;
        landAboveCounter = 0;
        waitCounter = 0;
    } else if ((getEnemiesCellValue(marioEgoRow + 8, marioEgoCol) == Sprite.KIND_BULLET_BILL
    || getEnemiesCellValue(marioEgoRow + 7, marioEgoCol) == Sprite.KIND_BULLET_BILL
    || getEnemiesCellValue(marioEgoRow + 5, marioEgoCol) == Sprite.KIND_BULLET_BILL
    || getEnemiesCellValue(marioEgoRow + 5, marioEgoCol + 1) == Sprite.KIND_BULLET_BILL)
    && (getReceptiveFieldCellValue(marioEgoRow + 1, marioEgoCol) == GeneralizerLevelScene.BORDER_HILL
    || getReceptiveFieldCellValue(marioEgoRow + 1, marioEgoCol + 1) == GeneralizerLevelScene.BORDER_HILL)
    || isTakeOff) {
        if (landAboveCounter < 16) {
            isTakeOff = true;
            action[Mario.KEY_RIGHT] = true;
            landAboveCounter ++;
            action[Mario.KEY_JUMP] = false;
        } else {
            action[Mario.KEY_JUMP] = isMarioAbleToJump || !isMarioOnGround;
            action[Mario.KEY_RIGHT] = true;
            landAboveCounter ++;

        }
    } else if ((landAboveCounter < 25 && landAboveCounter > 14)
        || waitCounter >= 20) {
        action[Mario.KEY_LEFT] = false;
        action[Mario.KEY_RIGHT] = true;
        action[Mario.KEY_JUMP] = true;
        action[Mario.KEY_SPEED] = true;
        landAboveCounter ++;
    } else if (landAboveCounter != 0 && isGap(marioEgoRow, marioEgoCol + 1)) {
        action[Mario.KEY_LEFT] = false;
        action[Mario.KEY_RIGHT] = false;
        action[Mario.KEY_JUMP] = false;
        waitCounter ++;
    }

    if (marioStatus == 2) {
        for (int i = 1; i < 8; i++) {
            if (getEnemiesCellValue(marioEgoRow, marioEgoCol + i) != 0) {
                action[Mario.KEY_SPEED] = isMarioAbleToShoot;
            }
        }
    }

    if ((
        isObstacle(marioEgoRow, marioEgoCol + 1) ||
        getEnemiesCellValue(marioEgoRow, marioEgoCol + 2) != Sprite.KIND_NONE ||
        getEnemiesCellValue(marioEgoRow, marioEgoCol + 1) != Sprite.KIND_NONE ||
        getEnemiesCellValue(marioEgoRow, marioEgoCol) != Sprite.KIND_NONE
        ))
    {
        action[Mario.KEY_JUMP] = isMarioAbleToJump || !isMarioOnGround;
    }

    if (getReceptiveFieldCellValue(marioEgoRow - 1, marioEgoCol)==GeneralizerLevelScene.BRICK) {
        action[Mario.KEY_JUMP] = isMarioAbleToJump || !isMarioOnGround;
    }
    if ((getActionForTube(marioEgoRow, marioEgoCol) || getActionForTube(marioEgoRow, marioEgoCol + 1))
        && flowerCounter <= 9) {
        action[Mario.KEY_LEFT] = false;
        action[Mario.KEY_RIGHT] = false;
        flowerCounter ++;
    } else if (flowerCounter > 9) {
        flowerCounter = 0;
    }

    if (
        getEnemiesCellValue(marioEgoRow - 1, marioEgoCol) == Sprite.KIND_BULLET_BILL ||
        getEnemiesCellValue(marioEgoRow - 2, marioEgoCol) == Sprite.KIND_BULLET_BILL ) {
        action[Mario.KEY_JUMP] = false;
    }

    if ((isObstacle(marioEgoRow, marioEgoCol + 1)
    && (getReceptiveFieldCellValue(marioEgoRow - 1, marioEgoCol)==GeneralizerLevelScene.BRICK
        || getReceptiveFieldCellValue(marioEgoRow - 2, marioEgoCol)==GeneralizerLevelScene.BRICK)) ||
        isTrapped) {
            if (getReceptiveFieldCellValue(marioEgoRow - 2, marioEgoCol)==GeneralizerLevelScene.BRICK) {
                action[Mario.KEY_RIGHT] = false;
                action[Mario.KEY_LEFT] = true;
                action[Mario.KEY_JUMP] = false;
                action[Mario.KEY_SPEED] = true;
                isTrapped = true;
            } else if (trappedCounter <= 5) {
                action[Mario.KEY_RIGHT] = true;
                action[Mario.KEY_LEFT] = false;
                action[Mario.KEY_JUMP] = true;
                action[Mario.KEY_SPEED] = true;
                trappedCounter++;
            } else {
                trappedCounter = 0;
                isTrapped = false;
            }
    }
    if (isObstacle(marioEgoRow, marioEgoCol + 1)) {
        action[Mario.KEY_UP] = true;
    }

    // if  (isLandBelow()){
    //     action[Mario.KEY_RIGHT] = false;
    //     action[Mario.KEY_LEFT] = false;
    //     action[Mario.KEY_SPEED] = false;
    //     action[Mario.KEY_JUMP] = false;
    //     action[Mario.KEY_DOWN] = true;
    // }
    return action;
}

}