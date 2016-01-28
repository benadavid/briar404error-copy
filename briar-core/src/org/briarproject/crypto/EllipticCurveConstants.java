package org.briarproject.crypto;

import org.spongycastle.asn1.x9.X9ECParameters;
import org.spongycastle.crypto.ec.CustomNamedCurves;
import org.spongycastle.crypto.params.ECDomainParameters;
import org.spongycastle.math.ec.ECCurve;
import org.spongycastle.math.ec.ECMultiplier;
import org.spongycastle.math.ec.ECPoint;
import org.spongycastle.math.ec.MontgomeryLadderMultiplier;

import java.math.BigInteger;

/** Parameters for curve curve25519 - see draft-turner-thecurve25519function. */
class EllipticCurveConstants {

	static final ECDomainParameters PARAMETERS;

	static {
		// Start with the default implementation of the curve
		X9ECParameters x9 = CustomNamedCurves.getByName("curve25519");
		// Use a constant-time multiplier
		ECMultiplier monty = new MontgomeryLadderMultiplier();
		ECCurve curve = x9.getCurve().configure().setMultiplier(monty).create();
		BigInteger gX = x9.getG().getAffineXCoord().toBigInteger();
		BigInteger gY = x9.getG().getAffineYCoord().toBigInteger();
		ECPoint g = curve.createPoint(gX, gY);
		// Convert to ECDomainParameters using the new multiplier
		PARAMETERS = new ECDomainParameters(curve, g, x9.getN(), x9.getH());
	}
}
