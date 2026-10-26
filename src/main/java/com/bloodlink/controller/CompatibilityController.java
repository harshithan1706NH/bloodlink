package com.bloodlink.controller;

import com.bloodlink.model.BloodGroup;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/** Exposes blood-transfusion compatibility rules for matching and user guidance. */
@RestController
@RequestMapping("/api/compatibility")
@CrossOrigin
public class CompatibilityController {
    private static final Map<BloodGroup, List<BloodGroup>> COMPATIBLE_DONORS = Map.ofEntries(
            Map.entry(BloodGroup.A_POSITIVE, List.of(BloodGroup.A_POSITIVE, BloodGroup.A_NEGATIVE,
                    BloodGroup.O_POSITIVE, BloodGroup.O_NEGATIVE)),
            Map.entry(BloodGroup.A_NEGATIVE, List.of(BloodGroup.A_NEGATIVE, BloodGroup.O_NEGATIVE)),
            Map.entry(BloodGroup.B_POSITIVE, List.of(BloodGroup.B_POSITIVE, BloodGroup.B_NEGATIVE,
                    BloodGroup.O_POSITIVE, BloodGroup.O_NEGATIVE)),
            Map.entry(BloodGroup.B_NEGATIVE, List.of(BloodGroup.B_NEGATIVE, BloodGroup.O_NEGATIVE)),
            Map.entry(BloodGroup.AB_POSITIVE, List.of(BloodGroup.A_POSITIVE, BloodGroup.A_NEGATIVE,
                    BloodGroup.B_POSITIVE, BloodGroup.B_NEGATIVE, BloodGroup.AB_POSITIVE,
                    BloodGroup.AB_NEGATIVE, BloodGroup.O_POSITIVE, BloodGroup.O_NEGATIVE)),
            Map.entry(BloodGroup.AB_NEGATIVE, List.of(BloodGroup.A_NEGATIVE, BloodGroup.B_NEGATIVE,
                    BloodGroup.AB_NEGATIVE, BloodGroup.O_NEGATIVE)),
            Map.entry(BloodGroup.O_POSITIVE, List.of(BloodGroup.O_POSITIVE, BloodGroup.O_NEGATIVE)),
            Map.entry(BloodGroup.O_NEGATIVE, List.of(BloodGroup.O_NEGATIVE)));

    @GetMapping("/{recipientBloodGroup}")
    public Compatibility compatibility(@PathVariable BloodGroup recipientBloodGroup) {
        return new Compatibility(recipientBloodGroup, COMPATIBLE_DONORS.get(recipientBloodGroup));
    }

    public record Compatibility(
            BloodGroup recipientBloodGroup,
            List<BloodGroup> compatibleDonorGroups) {
    }
}
