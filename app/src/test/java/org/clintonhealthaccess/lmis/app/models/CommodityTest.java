/*
 * Copyright (c) 2014, ThoughtWorks
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of the FreeBSD Project.
 */

package org.clintonhealthaccess.lmis.app.models;

import org.clintonhealthaccess.lmis.utils.RobolectricGradleTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

@RunWith(RobolectricGradleTestRunner.class)
public class CommodityTest {

    @Test
    public void shouldReturnTrueIfCommodityIsVial() throws Exception {
        Commodity commodity = new Commodity("BCG Vaccine 35_doses/vial");
        assertThat(commodity.isVial(), is(true));
    }

    @Test
    public void shouldReturnFalseIfCommodityIsNotVial() throws Exception {
        Commodity commodity = new Commodity("Panado");
        assertThat(commodity.isVial(), is(false));
    }

    @Test
    public void shouldReturnNumberOfDosesPerVial() throws Exception {
        Commodity commodity = new Commodity("BCG Vaccine 35_doses/vial");
        assertThat(commodity.dosesPerVial(), is(35));
    }

    @Test
    public void shouldReturnCorrectNumberOfDosesPerVial() throws Exception {
        Commodity commodity = new Commodity("BCG Vaccine 5_doses/vial");
        assertThat(commodity.dosesPerVial(), is(5));

    }

    @Test
    public void shouldReturnCorrectNumberOfDosesPerVialWhenCommodityIsNotVial() throws Exception {
        Commodity commodity = new Commodity("Panado");
        assertThat(commodity.dosesPerVial(), is(1));
    }
}