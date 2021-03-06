--------------------+
Experiment details: |
--------------------+

MACHINE: Dell Inspiron 15 5000 Series

IMPLEMENTATION: PEC-ANG

COMMENTS:

This example aims at showing how the approximate inference mechanism can be applied to larger domain descriptions than the one seen in the previous experiment.

DOMAIN DESCRIPTION:

	// AVATEA
	// Preliminary experiments
	// 
	// +----------------+
	// | Experiment n.4 |
	// +----------------+
	// Activities to detect:
	engagement takes-values {true, false}
	taskCorrect takes-values {true, false}
	// The subject is thought to be initially engaged
	// and carrying out the task correctly:
	initially-one-of { ({engagement=true,taskCorrect=true},1/1) }
	// Activities received from the classifier are:
	// 1. (Camera ->) Eyes not following target
	// 2. (EEG ->) Low level of attention
	// 3. (Motion sensor & Camera ->) Bad posture
	{ eyesNotFollowingTarget=false, lowAttention=false, badPosture=false }
		causes-one-of
		        { ({taskCorrect=true,engagement=true},4/10),
		          ({},6/10) }
	{ eyesNotFollowingTarget=true, lowAttention=false, badPosture=false, engagement=true }
		causes-one-of
		        { ({taskCorrect=false,engagement=false},3/100),
		          ({taskCorrect=false},17/100),
		          ({},8/10) }
	{ eyesNotFollowingTarget=true, lowAttention=false, badPosture=false, engagement=false }
		causes-one-of
			{ ({taskCorrect=false},3/10),
			  ({},7/10) }
	{ eyesNotFollowingTarget=false, lowAttention=true, badPosture=false }
		causes-one-of
			{ ({engagement=false},3/10),
			  ({},7/10) }
	{ eyesNotFollowingTarget=true, lowAttention=true, badPosture=false }
		causes-one-of
			{ ({taskCorrect=false,engagement=false},4/10),
			  ({},6/10) }
	{ eyesNotFollowingTarget=false, lowAttention=false, badPosture=true }
		causes-one-of
			{ ({taskCorrect=false,engagement=false},3/100),
			  ({taskCorrect=false},17/100),
			  ({},8/10) }
	{ eyesNotFollowingTarget=true, lowAttention=false, badPosture=true }
		causes-one-of
			{ ({taskCorrect=false,engagement=false},1/10),
			  ({taskCorrect=false},3/10),
			  ({},6/10) }
	{ eyesNotFollowingTarget=true, lowAttention=true, badPosture=true }
		causes-one-of
			{ ({taskCorrect=false,engagement=false},3/10),
			  ({taskCorrect=false},2/10),
			  ({},5/10) }
	// Sequence of events:
	lowAttention performed-at 0
	eyesNotFollowingTarget performed-at 0
	badPosture performed-at 0
	lowAttention performed-at 1
	eyesNotFollowingTarget performed-at 1
	badPosture performed-at 1
	eyesNotFollowingTarget performed-at 2 with-prob 76/100
	lowAttention performed-at 2 with-prob 87/100
	lowAttention performed-at 3
	eyesNotFollowingTarget performed-at 3
	badPosture performed-at 3
	eyesNotFollowingTarget performed-at 7 with-prob 7/100
	eyesNotFollowingTarget performed-at 8 with-prob 89/100
	eyesNotFollowingTarget performed-at 9 with-prob 74/100
	eyesNotFollowingTarget performed-at 11
	eyesNotFollowingTarget performed-at 12 with-prob 66/100
	eyesNotFollowingTarget performed-at 13 with-prob 43/100
	badPosture performed-at 13
	eyesNotFollowingTarget performed-at 14 with-prob 88/100
	eyesNotFollowingTarget performed-at 15 with-prob 7/100
	eyesNotFollowingTarget performed-at 16 with-prob 17/100
	eyesNotFollowingTarget performed-at 17 with-prob 8/100
	eyesNotFollowingTarget performed-at 18 with-prob 11/100
	badPosture performed-at 18 with-prob 1/10
	badPosture performed-at 19 with-prob 1/100
	lowAttention performed-at 19 with-prob 23/100
	badPosture performed-at 20 with-prob 13/100
	eyesNotFollowingTarget performed-at 20 with-prob 21/100
	badPosture performed-at 21 with-prob 11/100
	eyesNotFollowingTarget performed-at 20 with-prob 7/100
	lowAttention performed-at 22 with-prob 6/7
	eyesNotFollowingTarget performed-at 22 with-prob 24/100
	badPosture performed-at 22 with-prob 34/100
	lowAttention performed-at 23 with-prob 6/7
	eyesNotFollowingTarget performed-at 23 with-prob 24/100
	badPosture performed-at 23 with-prob 34/100
	lowAttention performed-at 24 with-prob 1/7
	lowAttention performed-at 25 with-prob 6/10
	eyesNotFollowingTarget performed-at 25 with-prob 99/100
	badPosture performed-at 25 with-prob 56/100
	lowAttention performed-at 26 with-prob 6/10
	eyesNotFollowingTarget performed-at 26 with-prob 99/100
	badPosture performed-at 26 with-prob 1/100
	lowAttention performed-at 27 with-prob 6/10
	eyesNotFollowingTarget performed-at 27 with-prob 99/100
	badPosture performed-at 27 with-prob 56/100
	eyesNotFollowingTarget performed-at 28 with-prob 89/100
	badPosture performed-at 28 with-prob 56/100
	eyesNotFollowingTarget performed-at 29
	badPosture performed-at 29

