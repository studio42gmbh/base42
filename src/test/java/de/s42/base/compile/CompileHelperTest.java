/*
 * Copyright Studio 42 GmbH 2021. All rights reserved.
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *  
 * For details to the License read https://www.s42m.de/license
 */
package de.s42.base.compile;

import java.lang.reflect.InvocationTargetException;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 * @author Benjamin Schiller
 */
public class CompileHelperTest
{

	public static interface TestAction
	{

		public String doAction();
	}

	protected final static String validClassName = "MyAction";
	protected final static String validClassCode = "public class MyAction implements de.s42.base.compile.CompileHelperTest.TestAction { public String doAction() { return \"YAY\"; } }";

	@Test
	public void validJitClassInstance() throws InvalidCompilation
	{
		TestAction testAction = CompileHelper.getCompiledInstance(validClassCode, validClassName);

		Assert.assertEquals("YAY", testAction.doAction());
	}

	@Test(expectedExceptions = InvalidCompilation.class)
	public void invalidJitClass() throws InvalidCompilation
	{
		String classCode = "public class MyAction implements de.s42.dl.util.compile.CompileHelperTest.TestAction {}";

		TestAction testAction = CompileHelper.getCompiledInstance(classCode, validClassName);
	}

	@Test
	public void validJitClassManualInstance() throws InvalidCompilation, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		Class testActionClass = CompileHelper.getCompiledClass(validClassCode, validClassName);

		TestAction testAction = (TestAction) testActionClass.getConstructor().newInstance();

		Assert.assertEquals("YAY", testAction.doAction());
	}

}
