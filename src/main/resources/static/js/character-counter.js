document.addEventListener("DOMContentLoaded", () => {

    const fields = document.querySelectorAll(".character-limited");

    fields.forEach((field) => {

        const wrapper =
            field.closest(".character-field-wrapper");

        if (!wrapper) {
            return;
        }

        const highlight =
            wrapper.querySelector(".character-highlight");

        /*
         * In both index.html and edit.html, the character-count
         * element immediately follows the wrapper.
         */
        const counter = wrapper.nextElementSibling;

        if (
            !highlight ||
            !counter ||
            !counter.classList.contains("character-count")
        ) {
            return;
        }

        const remainingDisplay =
            counter.querySelector(".characters-remaining");

        const counterLabel =
            counter.querySelector(".character-count-label");

        /*
         * The valid post limit comes from:
         *
         * data-maxlength="250"
         *
         * rather than hard-coding 250 into JavaScript.
         */
        const maxLength =
            Number(field.dataset.maxlength);

        const form =
            field.closest("form");

        /*
         * The new-post form uses an input[type="submit"],
         * while Edit uses a button[type="submit"].
         *
         * This selector works for both.
         */
        const submitButton =
            form?.querySelector(
                'input[type="submit"], button[type="submit"]'
            );


        // -----------------------------------------
        // CHARACTER COUNTER
        // -----------------------------------------

        const updateCounter = () => {

            const charactersUsed =
                field.value.length;

            const charactersRemaining =
                maxLength - charactersUsed;


            /*
             * User has gone OVER the allowed limit.
             *
             * Instead of:
             *
             * -12 characters left
             *
             * show:
             *
             * 12 characters over limit
             */
            if (charactersRemaining < 0) {

                remainingDisplay.textContent =
                    Math.abs(charactersRemaining);

                counterLabel.textContent =
                    "characters over limit";

                counter.classList.add("over-limit");
                counter.classList.remove(
                    "near-limit",
                    "at-limit"
                );

                field.setAttribute(
                    "aria-invalid",
                    "true"
                );

            /*
             * User has exactly reached 250.
             */
            } else if (charactersRemaining === 0) {

                remainingDisplay.textContent = 0;

                counterLabel.textContent =
                    "characters left";

                counter.classList.add("at-limit");
                counter.classList.remove(
                    "near-limit",
                    "over-limit"
                );

                field.setAttribute(
                    "aria-invalid",
                    "false"
                );

            /*
             * User is getting close to the limit.
             */
            } else if (charactersRemaining <= 30) {

                remainingDisplay.textContent =
                    charactersRemaining;

                counterLabel.textContent =
                    "characters left";

                counter.classList.add("near-limit");
                counter.classList.remove(
                    "at-limit",
                    "over-limit"
                );

                field.setAttribute(
                    "aria-invalid",
                    "false"
                );

            /*
             * Normal state.
             */
            } else {

                remainingDisplay.textContent =
                    charactersRemaining;

                counterLabel.textContent =
                    "characters left";

                counter.classList.remove(
                    "near-limit",
                    "at-limit",
                    "over-limit"
                );

                field.setAttribute(
                    "aria-invalid",
                    "false"
                );
            }


            /*
             * Prevent a normal user from submitting while
             * the post is too long.
             */
            if (submitButton) {
                submitButton.disabled =
                    charactersRemaining < 0;
            }
        };


        // -----------------------------------------
        // OVERFLOW HIGHLIGHTING
        // -----------------------------------------

        const updateHighlight = () => {

            const text = field.value;

            const allowedText =
                text.slice(0, maxLength);

            const overflowText =
                text.slice(maxLength);


            /*
             * Remove the previous mirrored content.
             */
            highlight.replaceChildren();


            /*
             * Put the first 250 characters into the mirror
             * as ordinary transparent text.
             *
             * This preserves the spacing/alignment.
             */
            highlight.append(
                document.createTextNode(allowedText)
            );


            /*
             * Anything beyond character 250 gets wrapped
             * in the highlighted span.
             */
            if (overflowText.length > 0) {

                const overflowSpan =
                    document.createElement("span");

                overflowSpan.classList.add(
                    "overflow-text"
                );

                overflowSpan.textContent =
                    overflowText;

                highlight.append(
                    overflowSpan
                );
            }


            /*
             * Helps preserve the last line correctly
             * if a textarea ends with a newline.
             */
            if (text.endsWith("\n")) {

                highlight.append(
                    document.createTextNode("\u200b")
                );
            }
        };


        // -----------------------------------------
        // KEEP MIRROR SCROLLING WITH THE FIELD
        // -----------------------------------------

        const syncScroll = () => {

            highlight.scrollTop =
                field.scrollTop;

            highlight.scrollLeft =
                field.scrollLeft;
        };


        // -----------------------------------------
        // UPDATE EVERYTHING
        // -----------------------------------------

        const updateField = () => {
            updateCounter();
            updateHighlight();
            syncScroll();
        };


        field.addEventListener(
            "input",
            updateField
        );

        field.addEventListener(
            "scroll",
            syncScroll
        );


        /*
         * Run immediately so Edit correctly calculates
         * the existing post when the page first loads.
         */
        updateField();
    });
});