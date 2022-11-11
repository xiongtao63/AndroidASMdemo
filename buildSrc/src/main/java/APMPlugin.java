import com.android.build.gradle.BaseExtension;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class APMPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        BaseExtension android = project.getExtensions().getByType(BaseExtension.class);

        //注册一个Transform

        android.registerTransform(new ASMTransform());
    }
}
