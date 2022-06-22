package me.whiteship.infleanthejavatest;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;

import java.lang.reflect.Method;

// lifecyle callback 두개 구현
// 오래 걸리는 test case를 찾아서 SlowTest 어노테이션을 사용하도록 권장하는 기능
public class FindSlowTestExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {

//    private static final long THRESHOLD = 1000L; // 1초

    private long THRESHOLD;

    public FindSlowTestExtension(long THRESHOLD) {
        this.THRESHOLD = THRESHOLD;
    }

    @Override
    public void beforeTestExecution(ExtensionContext extensionContext) throws Exception {
        String testClassName = extensionContext.getRequiredTestClass().getName();
        String testMethodName = extensionContext.getRequiredTestMethod().getName();
        ExtensionContext.Store store = extensionContext.getStore(ExtensionContext.Namespace.create(testClassName, testMethodName));

        store.put("START_TIME", System.currentTimeMillis());
    }

    @Override
    public void afterTestExecution(ExtensionContext extensionContext) throws Exception {

        // 리플렉션임. 이걸로 SlowTest 어노테이션이 이미 붙은 Test는 무시하게함
        Method requiredTestMethod = extensionContext.getRequiredTestMethod();
        SlowTest annotation = requiredTestMethod.getAnnotation(SlowTest.class);

        String testClassName = extensionContext.getRequiredTestClass().getName();
        String testMethodName = extensionContext.getRequiredTestMethod().getName();
        ExtensionContext.Store store = extensionContext.getStore(ExtensionContext.Namespace.create(testClassName, testMethodName));

        long start_time = store.remove("START_TIME", long.class);
        long duratoin = System.currentTimeMillis() - start_time;

        if( duratoin > THRESHOLD && annotation == null) {
            System.out.printf("Please consider mark method [%s] with @SlowTest.", testMethodName);
        }

    }

}
