package app.provider1.service;

import com.app.common.service.IUserService;
import com.nz.rpc.rpcsupport.annotation.RpcService;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;

@Service
@RpcService(interfaceClass=IUserService.class)
public class UserService  implements IUserService {

    @Override
    public String queryName(Long id) {
        return "UserService:"+id;
    }

    @Override
    public String queryName(String name, Long id) {
        return "UserService:"+id + " - " + name;
    }

    public static void main(String args[]){
        try {
            Class<?>[]  parameterTypes = new Class<?>[1];
            parameterTypes[0] = Long.class;
            Object[] parameters = new Object[1];
            System.out.println(parameterTypes[0]);
            parameters[0] = 1l;
            Class<?> clazz = Class.forName(UserService.class.getName());
            System.out.println(UserService.class.getName());
            Object target = clazz.newInstance();
            Method method = target.getClass().getDeclaredMethod("queryName", parameterTypes);

            Object result = method.invoke(target, parameters);
            System.out.println(result);
         //   response.setResult(result);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
