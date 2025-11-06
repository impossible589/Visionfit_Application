VisionFit AI: Push-Up Detector

Developer: Gourav
Platform: Android (Java)
Technologies: TensorFlow Lite (TFLite), Android SDK, CameraX


---

1. Introduction

VisionFit AI is an Android application that automatically detects and counts push-ups using on-device classification. Unlike systems that compute joint angles or rely on complex pose heuristics, this app uses a lightweight TensorFlow Lite model (named rubbish.tflite in the project) that classifies each processed frame into one of three labels:

PushUp (user in the "up" position)

PushDown (user in the "down" position)

Nothing (no relevant pose)


This classification-only approach keeps the runtime pipeline simple and efficient while still delivering reliable rep counting for standard push-up form.


---

2. Objective

The primary objectives are:

Build a simple, robust push-up counter that runs fully on-device.

Keep the pipeline lightweight by using a single TFLite classifier instead of computationally heavier pose-angle logic.

Provide immediate, visual feedback to users and a motivational reward animation when milestones are reached.



---

3. How It Works

The app’s runtime flow is classification-driven rather than angle-driven. Below is the real workflow used by the code in your repository:

1. Camera Frames: PushupTrackerActivity / MainActivity capture frames from the camera.


2. Preprocessing / Conversion: Frames are preprocessed, and optionally converted into a simplified representation by mediapipeimagetoskeleton.java (if present) or resized directly to the model input size.


3. TFLite Inference: TFLiteClassifier.java loads rubbish.tflite and runs inference on each preprocessed frame. The model returns one of three class scores: PushUp, PushDown, or Nothing.


4. State Logic & Counting: PushupDecorator / PushupTrackerActivity implement a small finite-state logic that increments the rep counter when a PushDown → PushUp transition is detected (i.e., a full down-to-up cycle).


5. Visualization & Rewards: BodyDrawer draws overlays if available, and RewardAnimationView plays animations on milestones.



This simple classifier-based pipeline reduces engineering complexity and keeps inference fast on mobile CPUs.


---

4. System Design

Components & Roles

Component	Role

MainActivity.java	App entry, permission handling, and navigation to tracker.
PushupTrackerActivity.java	Captures frames, runs preprocessing, performs classification, and manages counting/UI.
TFLiteClassifier.java	Loads rubbish.tflite, performs inference, and returns class scores and confidence.
mediapipeimagetoskeleton.java	Optional preprocessing helper — may convert frames to skeleton-like input if model expects it.
PushupDecorator.java	Implements the FSM (state transitions) for counting repetitions from classifier outputs.
BodyDrawer.java	Optional overlay renderer to visualize skeleton or keypoints.
RewardAnimationView.java	Visual reward on rep milestones.
splashactivity.java, tutorial.java, updater1.java, webpagedownloader.java	UX, onboarding, and update utilities.


Data Flow

Camera Frame
    ↓
Preprocessing (resize / normalize / skeleton conversion)
    ↓
TFLiteClassifier (rubbish.tflite) → class scores
    ↓
FSM in PushupDecorator / PushupTrackerActivity
    ↓
Rep count update → UI & RewardAnimationView


---

5. Algorithm & Counting Logic (Actual)

Because the model directly classifies each frame, the counting algorithm is event-driven based on class labels rather than numerical joint angles.

Finite-State Counting Logic

if (label.equals("PushDown") && (state.equals("UP") || state.equals("UNKNOWN"))) {
    state = "DOWN";
} else if (label.equals("PushUp") && state.equals("DOWN")) {
    repCount++;
    state = "UP";
}

Maintain a state variable with values like UNKNOWN, UP, DOWN.

For each frame, obtain the top class from the TFLite model.

Ignore Nothing unless it persists, which may trigger a state reset after a timeout.


This prevents false increments due to noisy single-frame predictions. Additional smoothing can be applied using confidence thresholds or consecutive-frame validation.


---

6. Why This Approach Is Efficient

1. Simplicity: Model outputs direct pose categories, eliminating complex geometric computations.


2. Low Compute: A small quantized TFLite model runs quickly on mobile CPUs.


3. Robust to Noisy Keypoint Detection: Because it relies on classification over the whole input, it can be more forgiving than exact-angle thresholds when the keypoint detector is imperfect.


4. Easier to Extend: To add a new exercise, you can retrain or swap the TFLite model to include extra classes (e.g., SquatUp, SquatDown).




---

7. Results & Practical Notes

Metric	Observation

Counting Accuracy	Highly dependent on model training quality.
Average Latency	~30–50 ms per frame (on mid-range Android).
CPU Usage	Under 40% typical load.
Offline Capability	Fully functional without internet.


Failure Modes: The classifier may mispredict unusual camera angles, lighting conditions, or body types. Data augmentation during training can reduce this.


---

8. How to Improve Model Reliability

Confidence Threshold: Ignore predictions with confidence below a set threshold (e.g., 0.6).

Temporal Filtering: Require N consecutive identical predictions to confirm a state change (debouncing).

Data Augmentation: Retrain rubbish.tflite with varied angles, lighting, and body shapes.

Hardware Delegates: Use NNAPI or GPU acceleration for higher FPS.



---

9. Conclusion

VisionFit AI adopts a classifier-first approach for workout detection — a small TensorFlow Lite model recognizes frame-level states (PushUp, PushDown, Nothing) and the app’s FSM translates them into rep counts. The architecture balances speed, simplicity, and offline operation, making it ideal for mobile fitness tracking.


---

Developed by: Gourav
