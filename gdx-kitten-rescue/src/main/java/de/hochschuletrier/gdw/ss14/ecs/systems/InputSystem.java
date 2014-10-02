package de.hochschuletrier.gdw.ss14.ecs.systems;

import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import de.hochschuletrier.gdw.ss14.ecs.EntityManager;
import de.hochschuletrier.gdw.ss14.ecs.components.CameraComponent;
import de.hochschuletrier.gdw.ss14.ecs.components.CatPropertyComponent;
import de.hochschuletrier.gdw.ss14.ecs.components.CirclePhysicsComponent;
import de.hochschuletrier.gdw.ss14.ecs.components.InputComponent;
import de.hochschuletrier.gdw.ss14.ecs.components.PlayerComponent;
import de.hochschuletrier.gdw.ss14.input.GameInputAdapter;
import de.hochschuletrier.gdw.ss14.input.InputManager;

public class InputSystem extends ECSystem implements GameInputAdapter
{
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(InputSystem.class);

    public InputSystem(EntityManager entityManager)
    {
        super(entityManager, 1);
        InputManager.getInstance().addGameInputAdapter(this);
    }

    @Override
    public void update(float delta)
    {
        // TODO Auto-generated method stub
        Array<Integer> compos = entityManager.getAllEntitiesWithComponents(InputComponent.class, CameraComponent.class, PlayerComponent.class);

        for (Integer integer : compos)
        {
            InputComponent inputCompo = entityManager.getComponent(integer, InputComponent.class);
            CameraComponent camComp = entityManager.getComponent(integer, CameraComponent.class);
            CirclePhysicsComponent laser = entityManager.getComponent(integer, CirclePhysicsComponent.class);
            inputCompo.whereToGo = new Vector2(Gdx.input.getX(), Gdx.input.getY());
            Vector3 vec = new Vector3(inputCompo.whereToGo.x, inputCompo.whereToGo.y, 1);
//          vec = vec.mul(DrawUtil.batch.getProjectionMatrix());
//          inputCompo.whereToGo = new Vector2(vec.x,vec.y);
            vec = camComp.smoothCamera.getOrthographicCamera().unproject(vec);
            inputCompo.whereToGo = new Vector2(vec.x, vec.y);
        }
    }

    @Override
    public void render()
    {
    }

    @Override
    public void move(int screenX, int screenY)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void moveUp(float scale)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void moveDown(float scale)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void moveLeft(float scale)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void moveRight(float scale)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void laserButtonPressed()
    {
        //logger.debug("Laser button pressed!");

        Array<Integer> entities = entityManager.getAllEntitiesWithComponents(CatPropertyComponent.class);

        for (Integer entity : entities)
        {
            CatPropertyComponent catPropertyComponent = entityManager.getComponent(entity, CatPropertyComponent.class);

            if (catPropertyComponent.canSeeLaserPointer == true)
            {
                catPropertyComponent.canSeeLaserPointer = false;
            }
            else
            {
                catPropertyComponent.canSeeLaserPointer = true;
            }
        }

    }

    @Override
    public void waterPistolButtonDown()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void waterPistolButtonUp()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void menueButtonPressed()
    {
        // TODO Auto-generated method stub

    }
}
