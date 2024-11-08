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
 
 public class OwnAgent2 extends BasicMarioAIAgent implements Agent
 {
 int jumpCounter = 0;
 boolean isJumpingOverGap = false;
 int enemyFallingCounter = 0;
 int pitCounter = 0;
 boolean isJumpingOverPit = false;

 public OwnAgent2()
 {
     super("OwnAgent2");
     reset();
 }

 public void reset()
 {
     action = new boolean[Environment.numberOfKeys];
     action[Mario.KEY_RIGHT] = true;
     jumpCounter = 0;
     isJumpingOverGap = false;
     enemyFallingCounter = 0;
     pitCounter = 0;
     isJumpingOverPit = false;
 }

 public boolean isObstacle(int r, int c){
     return getReceptiveFieldCellValue(r, c)==GeneralizerLevelScene.BRICK
             || getReceptiveFieldCellValue(r, c)==GeneralizerLevelScene.BORDER_CANNOT_PASS_THROUGH
             || getReceptiveFieldCellValue(r, c)==GeneralizerLevelScene.FLOWER_POT_OR_CANNON
             || getReceptiveFieldCellValue(r, c)==GeneralizerLevelScene.LADDER;
 }

 private boolean isGap(int r, int c) {
     for (int i = 1; i < 5; i++) {
         if (isObstacle(r + i, c)) {
             return false;
         }
     }
     return true;
 }

 public boolean isEnemyAbove(int r, int c) {
    for (int i = 3; i < 8; i++) {
        if (getEnemiesCellValue(r - i, c) != Sprite.KIND_NONE) {
            return true;
        }
    }
    return false;
}

 public boolean[] getAction()
 {
     action[Mario.KEY_SPEED] = false;
     action[Mario.KEY_UP] = false;

     if (!isObstacle(marioEgoRow + 1, marioEgoCol + 1)) {
        action[Mario.KEY_JUMP] = isMarioAbleToJump || !isMarioOnGround;
     }

     if (isGap(marioEgoRow, marioEgoCol + 1) ||
        (isGap(marioEgoRow, marioEgoCol + 2))) {
         if (isMarioOnGround) {
             if (jumpCounter >= 5) {
                 action[Mario.KEY_SPEED] = true;
                 action[Mario.KEY_RIGHT] = true;
                 action[Mario.KEY_LEFT] = false;
                 if (isGap(marioEgoRow, marioEgoCol + 1)){
                    action[Mario.KEY_JUMP] = isMarioAbleToJump || !isMarioOnGround;
                 }
                 isJumpingOverGap = true;
                 jumpCounter = 0;
             } else {
                 action[Mario.KEY_RIGHT ] = false;
                 action[Mario.KEY_LEFT] = false;
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

     if (marioStatus == 2) {
         for (int i = 1; i < 8; i++) {
             if (getEnemiesCellValue(marioEgoRow, marioEgoCol + i) != 0) {
                 action[Mario.KEY_SPEED] = isMarioAbleToShoot;
             }
         }
     }

     if (
         isObstacle(marioEgoRow, marioEgoCol + 1) ||
         isObstacle(marioEgoRow - 1, marioEgoCol + 1) ||
         getEnemiesCellValue(marioEgoRow, marioEgoCol + 2) != Sprite.KIND_NONE ||
         getEnemiesCellValue(marioEgoRow, marioEgoCol + 1) != Sprite.KIND_NONE ||
         getEnemiesCellValue(marioEgoRow, marioEgoCol) != Sprite.KIND_NONE
         ){
         action[Mario.KEY_JUMP] = isMarioAbleToJump || !isMarioOnGround;
     }

     if ((
        getEnemiesCellValue(marioEgoRow + 1, marioEgoCol) == Sprite.KIND_GOOMBA ||
        getEnemiesCellValue(marioEgoRow + 1, marioEgoCol + 1) == Sprite.KIND_GOOMBA ||
        getEnemiesCellValue(marioEgoRow + 2, marioEgoCol + 1) == Sprite.KIND_GOOMBA ||
        getEnemiesCellValue(marioEgoRow + 1, marioEgoCol + 2) == Sprite.KIND_GOOMBA ||
        getEnemiesCellValue(marioEgoRow + 2, marioEgoCol + 2) == Sprite.KIND_GOOMBA
     ) && pitCounter <= 8 && !isMarioOnGround) {
        action[Mario.KEY_LEFT] = true;
        action[Mario.KEY_RIGHT] = false;
        pitCounter++;
        isJumpingOverPit = true;
     } else if (pitCounter > 8) {
        pitCounter = 0;
        isJumpingOverPit = false;
     } else if (isJumpingOverPit) {
        action[Mario.KEY_LEFT] = false;
        action[Mario.KEY_RIGHT] = true;
     }


    //  if (isEnemyAbove(marioEgoRow, marioEgoCol) ||
    //     isEnemyAbove(marioEgoRow, marioEgoCol + 1) ||
    //     isEnemyAbove(marioEgoRow, marioEgoCol + 2) ||
    //     isEnemyAbove(marioEgoRow, marioEgoCol + 3)) {
    //     action[Mario.KEY_LEFT] = true;
    //     action[Mario.KEY_RIGHT] = false;
    // }
     return action;
    }
 }