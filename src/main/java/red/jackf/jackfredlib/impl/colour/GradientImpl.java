package red.jackf.jackfredlib.impl.colour;

import red.jackf.jackfredlib.api.colour.Colour;
import red.jackf.jackfredlib.api.colour.Gradient;
import red.jackf.jackfredlib.api.colour.GradientBuilder;

import java.util.Collections;
import java.util.NavigableMap;
import java.util.TreeMap;

public class GradientImpl implements Gradient {
    private final NavigableMap<Float, Colour> frames;

    public GradientImpl(NavigableMap<Float, Colour> frames) {
        this.frames = frames;
    }

    @Override
    public Colour sample(float progress) {
        progress = Gradient.wrapPoint(progress);
        var start = frames.floorEntry(progress);
        if (start.getKey() == progress) return start.getValue();
        var end = frames.ceilingEntry(progress);
        var range = end.getKey() - start.getKey();
        var factor = (progress - start.getKey()) / range;
        return start.getValue().lerp(end.getValue(), factor);
    }

    @Override
    public Gradient slice(float start, float end) {
        start = Gradient.wrapPoint(start);
        end = Gradient.wrapPoint(end);
        if (start == end) return this.sample(GradientBuilder.START); // solid colour
        else if (start < end) {
            var newFrames = new TreeMap<Float, Colour>();
            var factor = 1 / (end - start);

            for (var entry : this.frames.subMap(start, true, end, true).entrySet())
                newFrames.put((entry.getKey() - start) * factor, entry.getValue());

            // clear any frames above Builder.END, just in case floating point errors took it above
            newFrames.tailMap(GradientBuilder.END, false).clear();

            if (!newFrames.containsKey(GradientBuilder.START)) newFrames.put(GradientBuilder.START, this.sample(start));
            if (!newFrames.containsKey(GradientBuilder.END)) newFrames.put(GradientBuilder.END, this.sample(end));
            return new GradientImpl(newFrames);
        } else { // start > end
            var startPoints = slice(start, GradientBuilder.END).getPoints();
            var endPoints = slice(GradientBuilder.START, end).getPoints();
            var startRange = GradientBuilder.END - start;
            var range = startRange + end;
            var startScaleFactor = startRange / range;
            var endScaleFactor = end / range;

            var newFrames = new TreeMap<Float, Colour>();
            for (var entry : startPoints.entrySet())
                newFrames.put((entry.getKey() - start) / startScaleFactor, entry.getValue());
            for (var entry : endPoints.entrySet())
                newFrames.put(entry.getKey() / endScaleFactor + startRange, entry.getValue());

            // clear any frames above Builder.END, just in case floating point errors took it above
            newFrames.tailMap(GradientBuilder.END, false).clear();

            if (!newFrames.containsKey(GradientBuilder.START)) newFrames.put(GradientBuilder.START, this.sample(start));
            if (!newFrames.containsKey(GradientBuilder.END)) newFrames.put(GradientBuilder.END, this.sample(end));
            return new GradientImpl(newFrames);
        }
    }

    @Override
    public Gradient reversed() {
        var map = new TreeMap<Float, Colour>();
        for (var point : this.frames.entrySet())
            map.put(GradientBuilder.END - point.getKey(), point.getValue());

        return new GradientImpl(map);
    }

    @Override
    public NavigableMap<Float, Colour> getPoints() {
        return Collections.unmodifiableNavigableMap(this.frames);
    }
}
