/*
Copyright (c) 2012, Apps4Av Inc. (apps4av.com) 
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
    *     * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
    *
    *     THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, LONGITUDE, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package com.chartsack.charts;

/**
 * 
 * @author zkhan
 *
 */
public class Multivariate {

	//A Simple Expression for Multivariate Lagrange Interpolation
	//Kamron Saniee 2007
	//n (order) = 1, m(variables) = 2, hence points needed = fact(m+n, n) = fact(3, 1) = 3 

	private double x0;
	private double x1;
	private double x2;
	private double y0;
	private double y1;
	private double y2;
	private double z0;
	private double z1;
	private double z2;
	private double m[][] = new double[3][3];
	private double m0[][] = new double[3][3];
	private double m1[][] = new double[3][3];
	private double m2[][] = new double[3][3];
	private double d;

	/**
	 * Find det of the matrix size 3x3
	 * @param mm
	 * @return
	 */
	private double det(double[][] mm) {
		double ret = 
				mm[0][0] * (mm[1][1] * mm[2][2] - mm[2][1] * mm[1][2]) -
				mm[1][0] * (mm[0][1] * mm[2][2] - mm[2][1] * mm[0][2]) +
				mm[2][0] * (mm[0][1] * mm[1][2] - mm[1][1] * mm[0][2])
				;
		return ret;
	}

	/**
	 * 
	 * @param xi0
	 * @param yi0
	 * @param zi0
	 * @param xi1
	 * @param yi1
	 * @param zi1
	 * @param xi2
	 * @param yi2
	 * @param zi2
	 */
	public Multivariate(double xi0, double yi0, double zi0, double xi1, double yi1, double zi1, double xi2, double yi2, double zi2) {
		//zi = a0xi + a1yi + a3
		// three points
		x0 = xi0;
		y0 = yi0;
		z0 = zi0;
		x1 = xi1;
		y1 = yi1;
		z1 = zi1;
		x2 = xi2;
		y2 = yi2;
		z2 = zi2;

		// m is fixed
		m[0][0] = x0;
		m[1][0] = y0;
		m[2][0] = 1;

		m[0][1] = x1;
		m[1][1] = y1;
		m[2][1] = 1;

		m[0][2] = x2;
		m[1][2] = y2;
		m[2][2] = 1;

		// so is its det, equation 4
		d = det(m);
	}
	
	/**
	 * Find z value based on x, y
	 * @param x
	 * @param y
	 * @return
	 */
	public double interpolate(double x, double y) {

		// equation 5
		// m0
		
		m0[0][0] = x;
		m0[1][0] = y;
		m0[2][0] = 1;

		m0[0][1] = x1;
		m0[1][1] = y1;
		m0[2][1] = 1;

		m0[0][2] = x2;
		m0[1][2] = y2;
		m0[2][2] = 1;

		// m1
		
		m1[0][0] = x0;
		m1[1][0] = y0;
		m1[2][0] = 1;

		m1[0][1] = x;
		m1[1][1] = y;
		m1[2][1] = 1;

		m1[0][2] = x2;
		m1[1][2] = y2;
		m1[2][2] = 1;

		// m2
		
		m2[0][0] = x0;
		m2[1][0] = y0;
		m2[2][0] = 1;

		m2[0][1] = x1;
		m2[1][1] = y1;
		m2[2][1] = 1;

		m2[0][2] = x;
		m2[1][2] = y;
		m2[2][2] = 1;

		// determinant
		double d0 = det(m0);
		double d1 = det(m1);
		double d2 = det(m2);
		
		// equation 7
		double z = z0 * d0 / d + z1 * d1 / d + z2 * d2 / d;
		
		// each interpolate requires 30 multiplies, and 3 divides
		
		return z;
	}
}
