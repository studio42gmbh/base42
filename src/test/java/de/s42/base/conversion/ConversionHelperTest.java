/*
 * Copyright Studio 42 GmbH 2020. All rights reserved.
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *  
 * For details to the License read https://www.s42m.de/license
 */
package de.s42.base.conversion;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 * @author Benjamin Schiller
 */
public class ConversionHelperTest
{

	@Test
	public void convertArray()
	{
		Object[] source = {1.1f, 2.2f, 3.3f};
		Float[] target = (Float[]) ConversionHelper.convert(source, Float[].class);
		float sum = 0.0f;
		for (Float v : target) {
			sum += v;
		}
		//epsilon tets
		Assert.assertTrue(Math.abs(sum - 6.6f) < 0.0001);
	}
}
